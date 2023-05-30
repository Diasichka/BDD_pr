package bdd_utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BDDUtl {


    public static BDD BDD_create(String dnf, String varOrder) {

        char[] variableOrder = varOrder.strip().toCharArray();
        int numVariables = variableOrder.length;
        boolean[] function = new boolean[(int) Math.pow(2, numVariables)];

        int[] sizes = new int[function.length + 1];
        Map<String, Integer> val;
        BDD graphBDD = new BDD(numVariables);

        // парсим ДНФ-строку
        String[] conjunctions = dnf.split("\\+");

        for (String conjunction : conjunctions) {
            conjunction = conjunction.strip();
            boolean[] clause = new boolean[numVariables];
            boolean[] idx = new boolean[numVariables];

            for (int i = 0; i < conjunction.length(); i++) {
                char c = conjunction.charAt(i);

                if ((c != '(') && (c != ')')) {
                    if (c == '!') {
                        i++;
                        c = conjunction.charAt(i);
                        if ((c != '(') && (c != ')')) {
                            //System.out.println("VarOrder: " + varOrder + " Conjunction: " + conjunction + " Var: " + conjunction.charAt(i));
                            clause[indexOfVariable(variableOrder, c)] = false;
                            idx[indexOfVariable(variableOrder, c)] = true;
                        }
                    } else {
                        //System.out.println("VarOrder: "+ varOrder + " Conjunction: " + conjunction + " Var: " + c);
                        clause[indexOfVariable(variableOrder, c)] = true;
                        idx[indexOfVariable(variableOrder, c)] = true;
                    }
                }
            }

            //int index = toIndex(clause);
            function = toIndex2(clause, idx, function);
            //function[index] = true;
        }

        //showTable(varOrder, function);


        int root = graphBDD.addKnot(0, -1,0, -1, -1);
        BDDKnot currKnot, low, high;

        sizes[0] = 1;

        for (int i = 0; i < numVariables; i++) {
            int variable = i;

            //String st = "Var: " + variableOrder[i];
            //String buf = "Var: " + variableOrder[i];

            for (int j = 0; j < (int) Math.pow(2, i); j++) {

                val = checkTerm(variable, j, variableOrder.length, function);
                //st += " ( knot: " + j + " low: " + val.get("low") + " high: " + val.get("high") + " )";

                currKnot = graphBDD.graph.get((int)Math.pow(2, i)-1+j);
                if(currKnot == null) continue;

                int idx = (int)Math.pow(2, i+1)-1 + j*2;

                if (val.get("low") == -1) {
                    currKnot.setLow(graphBDD.addKnot(idx, currKnot.index, variable, -1, -1));
                    sizes[i + 1] += 1;
                } else {
                    currKnot.setLTerm(val.get("low"));
                }
                if (val.get("high") == -1) {
                    currKnot.setHigh(graphBDD.addKnot(idx+1, currKnot.index, variable, -1, -1));
                    sizes[i + 1] += 1;
                } else {
                    currKnot.setRTerm(val.get("high"));
                }
                graphBDD.graph.replace(currKnot.index, currKnot);

                //buf += "{ knot-"+ currKnot.index + "( l:" + currKnot.getLow() + " h:"+currKnot.getHigh()+" )"
                //        +" ( t0: "+ currKnot.getLTerm() + " t1: "+currKnot.getRTerm()+ " ) }";

            }
            //System.out.println(st);
            //System.out.println(buf);

            int start =(int)Math.pow(2, i)-1;
            int end =(int)Math.pow(2, i+1)-1;

            for (int j = start; j < end; j += 2) {
                low = graphBDD.graph.get(j);
                high = graphBDD.graph.get(j+1);
                if((low != null) && (high != null))
                    if ((low.getLTerm() != -1) && (low.getRTerm() != -1)) {
                        if ((low.getLTerm() == high.getLTerm()) && (low.getRTerm() == high.getRTerm())) {

                            graphBDD.graph.remove(high.index);
                            high = graphBDD.graph.get(low.parent);
                            high.setHigh(low.index);
                            graphBDD.graph.replace(high.index, high);
                            sizes[i + 1] -= 1;
                        }
                    }
            }

        }

/*
        for (int i = 0; i < numVariables; i++) {
            int variable = i;
            String buf = "Var: " + variableOrder[i];
            for (int j = 0; j < (int) Math.pow(2, i); j++) {
                currKnot = graphBDD.graph.get((int) Math.pow(2, i) - 1 + j);
                if (currKnot == null) continue;

                buf += "{ knot-" + currKnot.index + "( l:" + currKnot.getLow() + " h:" + currKnot.getHigh() + " )"
                    + " ( t0: " + currKnot.getLTerm() + " t1: " + currKnot.getRTerm() + " ) }";
            }
            System.out.println(buf);
        }
*/
        return graphBDD;
    }


    private static void showTable(String varOrder, boolean[] function) {
        char[] variableOrder = varOrder.strip().toCharArray();
        String str = "----";
        for (int i = 0; i < variableOrder.length; i++)
            str += (variableOrder[i] + "-");
        System.out.println(str);
        for (int i = 0; i < function.length; i++){
            str = i + " | ";
            for (int z = 0; z < variableOrder.length; z++)
                str += ( (i % (int) Math.pow(2, (variableOrder.length - z))) /  (int) Math.pow(2, (variableOrder.length - (z+1)))  + " " );
            if (function[i])
                System.out.println(  str + " | " + 1 );
            else
                System.out.println(  str + " | " + 0 );
        }
    }

    // Она вычисляет значение левого и правого потомков для данной переменной и возвращает результат в виде Map<String, Integer>,
    // где ключ "low" относится к значению левого потомка, а ключ "high" - к значению правого потомка.
    private static Map<String, Integer> checkTerm(int variable, int nVal, int varLen, boolean[] function){
        Map<String, Integer> val = new HashMap<>();
        val.put("low", -1);
        val.put("high", -1);

        int start = nVal* (int)Math.pow(2, varLen-variable);
        int part = function.length / (int) Math.pow(2, variable+1);
        boolean f0 = true, f1 = true;

        for(int i = 0; i < part; i++){
            if(function[i+start]) f0 = false;
            else f1 = false;
        }
        if(f1) val.replace("low", 1);
        if(f0) val.replace("low", 0);

        f0 = true;
        f1 = true;
        for(int i = 0; i < part; i++){
            if(function[i + start+ part]) f0 = false;
            else f1 = false;
        }
        if(f1) val.replace("high", 1);
        if(f0) val.replace("high", 0);

        return val;
    }
    private static int indexOfVariable(char[] variableOrder, char variable) {

        for (int i = 0; i < variableOrder.length; i++) {
            if (variableOrder[i] == variable) {
                return i;
            }
        }

        return -1;
    }

    private static int toIndex(boolean[] clause) {
        int index = 0;
        for (int i = 0; i < clause.length; i++) {
            if (clause[i]) {
                index += Math.pow(2, (clause.length - 1) - i);
            }
        }
        //System.out.println("Index: " + index);
        return index;
    }

    private static boolean[] toIndex2(boolean[] clause, boolean [] idx, boolean[] function){
        boolean [] table = new boolean[clause.length ];
        String st, buf;
        boolean flag;
        boolean [] resTable = function;

        for(int i = 0; i < (int)Math.pow(2, clause.length); i++){
            //st = ""+ i + " :";
            for (int z = 0; z < clause.length; z++) {
                if (((i % (int) Math.pow(2, (clause.length - z))) / (int) Math.pow(2, (clause.length - (z + 1)))) == 0)
                    table[z] = false;
                else table[z] = true;
                //st += table[z] + " ";
            }

            flag = true;

            for(int j = 0; j < clause.length; j++) {
                if (idx[j]) {

                    if (!(clause[j] == table[j])) {
                        flag = false;

                    }
                }
            }
            if(flag) {
                resTable[toIndex(table)] = true;
                //System.out.println(st);
            }
            //System.out.println(buf);
            //System.out.println(st);
        }

        return resTable;
    }

    public static char BDD_use(String val, Map<Integer, BDDKnot> gr){
        char res = '2';
        char c;
        int number;
        BDDKnot knot;

        knot = gr.get(0);
        for(int i =0; i < val.length(); i++){
            c = val.charAt(i);
            number = Character.digit(c, 10);
            if ( number == 0){
                if(knot.getLTerm() == -1){
                    knot = gr.get(knot.getLow());
                    continue;
                }
                else return res = (char) (knot.getLTerm() +'0');
            }
            else if (number == 1){
                if(knot.getRTerm() == -1){
                    knot = gr.get(knot.getHigh());
                    continue;
                }
                else return res = (char) (knot.getRTerm() +'0');
            }
        }
        return res;
    }

    public static int getVarNum(String dnf) {

        //Determine variable order
        Map<String, Integer> variables = new HashMap<>();

        String[] varNames = dnf.replaceAll("[^a-zA-Z]+", "").split("");
        String bestOrder, newStr;

        int numVars, bestOrderKnots;

        //System.out.println(varNames);
        for (int i = 0; i < varNames.length; i++) {
            variables.put(varNames[i], i);
        }

        bestOrder = variables.keySet().toString();
        bestOrder = bestOrder.replaceAll("[^A-Za-z]", "");
        //System.out.println("Order: " + bestOrder);
        numVars = bestOrder.length();
        return numVars;
    }

    public static BDD BDD_create_with_best_order(String dnf){
        BDD bdd;
        //Determine variable order
        Map<String, Integer> variables = new HashMap<>();
        int range = 100; // -1

        String[] varNames = dnf.replaceAll("[^a-zA-Z]+", "").split("");
        String bestOrder, newStr ;
        char[] chars;
        int numVars, bestOrderKnots;

        //System.out.println(varNames);
        for (int i = 0; i < varNames.length; i++) {
            variables.put(varNames[i], i);
        }

        bestOrder = variables.keySet().toString();
        bestOrder = bestOrder.replaceAll("[^A-Za-z]", "");
        //System.out.println("Order: " + bestOrder);
        numVars = bestOrder.length();
        chars = bestOrder.toCharArray();

        bdd = BDD_create(dnf, bestOrder);
        bestOrderKnots = bdd.graph.size();

        // Инициализируем массив индексов
        int[] indexes = new int[numVars];
        for (int i = 0; i < numVars; i++) {
            indexes[i] = 0;
        }


        int i = 0;
        int counter = 0;
        while (i < numVars) {
            if (indexes[i] < i) {
                if (i % 2 == 0) {
                    // Меняем местами первый и i-й символ
                    char temp = chars[0];
                    chars[0] = chars[i];
                    chars[i] = temp;
                } else {
                    // Меняем местами indexes[i] и i-й символ
                    char temp = chars[indexes[i]];
                    chars[indexes[i]] = chars[i];
                    chars[i] = temp;
                }
                // Создаем новую строку из массива символов
                newStr = new String(chars);
                //System.out.println("Order: " + newStr);

                // Вызываем функцию BDDcreate с новой строкой в качестве параметра
                bdd = BDD_create(dnf, newStr);
                //System.out.println("Order: " + newStr + " Knots: " + bdd.graph.size());

                if(bestOrderKnots > bdd.graph.size()){
                    bestOrderKnots = bdd.graph.size();
                    bestOrder = newStr;
                }

                indexes[i]++;
                i = 0;
                counter++;
                if (range != -1)
                    if(counter >= range) i = numVars;
            } else {
                indexes[i] = 0;
                i++;
            }
        }

        //generateOrder("", bestOrder);


        return BDD_create(dnf, bestOrder);
    }
    public static void generateOrder(String prefix, String str) {
        int n = str.length();
        if (n == 0) {
            System.out.println(prefix);
        } else {
            for (int i = 0; i < n; i++) {
                generateOrder(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n));
            }
        }
    }

    public static String genFuncDNF(String var, int numClauses, int clauseLength){
        Random random = new Random();
        StringBuilder dnf = new StringBuilder();
        char[] variableOrder = var.strip().toCharArray();

        for (int i = 0; i < numClauses; i++) {
            StringBuilder clause = new StringBuilder();
            int length = random.nextInt(clauseLength) + 1;

            for (int j = 0; j < length; j++) {
                int index = random.nextInt(variableOrder.length);
                if (clause.toString().indexOf(variableOrder[index]) == -1)
                    clause.append(random.nextBoolean() ? "" : "!").append(variableOrder[index]);
            }

            dnf.append("(").append(clause).append(")").append(i == numClauses - 1 ? "" : " +");
        }

        return dnf.toString();
    }

    // Перевод десятичного числа в двоичную строку заданной длины
    public static String decToBin(int decimalNumber, int desiredLength) {
        String binary = Integer.toBinaryString(decimalNumber);
        int padding = desiredLength - binary.length();
        if (padding > 0) {
            binary = "0".repeat(padding) + binary;
        }
        return binary;
    }

    public static double getCompressRate(int varNum, int knots) {
        double compression = 100- (double)knots/Math.pow(2, varNum)*100;
        return compression;
    }


}
