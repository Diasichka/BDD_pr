import bdd_utils.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        long startTime, endTime;
        Random random = new Random();
        BDD bdd;
        String variableOrder = "abcd";
        String dnf = "abc + !abd + !bcd";
        int test_val = 100;
        int errorNum = 0;
        double cSumm = 0;
        double reduce, reduceMax, reduceMin;

        bdd = BDDUtl.BDD_create(dnf, variableOrder);

    //-------------------------------------------------------------------------------------------------
        int numClauses = 5; // Количество дизъюнктов в ДНФ
        //variableOrder = "abcdefghjkolpmnrt"; //17
        variableOrder = "abcdefghjkolp";    //13
        //variableOrder = "abcdefghjkolpm";    //14
        //variableOrder = "abcdefghjkolpm";    //15
        //variableOrder = "abcdefghjkolpmr";    //16

        int clauseLength = variableOrder.length(); // Максимальная длина дизъюнкта

        cSumm = 0;
        reduceMax = 0;
        reduceMin = 100;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < test_val; i++) {
            dnf = BDDUtl.genFuncDNF(variableOrder, numClauses, clauseLength);
            //System.out.println(dnf);
            bdd = BDDUtl.BDD_create(dnf, variableOrder);

            reduce = BDDUtl.getCompressRate(bdd.getNumVariables(), bdd.getSize());
            cSumm += reduce;
            if(reduce > reduceMax) reduceMax = reduce;
            if(reduce < reduceMin) reduceMin = reduce;
        }
        endTime = System.currentTimeMillis();
        System.out.println("\n Avarage time creation (ms): " + (endTime - startTime)/test_val);
        //System.out.println("\n Avarage time creation (ms): " + (endTime - startTime));
        System.out.println("\n Compression average rate: " + cSumm/test_val);
        System.out.println(" Compression min: " + reduceMin);
        System.out.println(" Compression max: " + reduceMax);
        System.out.println("\n ____________________________________________________________________");
//------------------------------------------------------------------------------------------
        cSumm = 0;
        reduceMax = 0;
        reduceMin = 100;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < test_val; i++) {
            do
                dnf = BDDUtl.genFuncDNF(variableOrder, numClauses, clauseLength);
            while( BDDUtl.getVarNum(dnf) < variableOrder.length());
            bdd = BDDUtl.BDD_create_with_best_order(dnf);

            reduce = BDDUtl.getCompressRate(bdd.getNumVariables(), bdd.getSize());
            cSumm += reduce;
            if(reduce > reduceMax) reduceMax = reduce;
            if(reduce < reduceMin) reduceMin = reduce;
        }
        endTime = System.currentTimeMillis();
        System.out.println("\n Avarage time search order (ms): " + (endTime - startTime)/test_val);
        //System.out.println("\n Avarage time search order (ms): " + (endTime - startTime));
        System.out.println("\n Compression rate: " + cSumm/test_val);
        System.out.println(" Compression min: " + reduceMin);
        System.out.println(" Compression max: " + reduceMax);
        System.out.println("\n ____________________________________________________________________");

    //------------------------------------------------------------------------------------------
        int resCircle = 100;
        errorNum = 0;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < test_val; i++) {
            dnf = BDDUtl.genFuncDNF(variableOrder, numClauses, clauseLength);

            bdd = BDDUtl.BDD_create(dnf, variableOrder);
            for (int j = 0; j < resCircle; j++) {
                String st = BDDUtl.decToBin(j, variableOrder.length());
                if (BDDUtl.BDD_use(st, bdd.graph) == '2') errorNum++;
            }
        }
        endTime = System.currentTimeMillis();
        //System.out.println("\n Avarage time get result (ms): " + (endTime - startTime)/test_val/resCircle);
        System.out.println("\n Avarage time get result (ms): " + (endTime - startTime)/test_val);
        System.out.println(" Number of errors: " + errorNum);
        System.out.println("\n ____________________________________________________________________");
 /*
        //String dnf = "ab + cd ";
        BDD bdd = BDDUtl.BDD_create(dnf, variableOrder);
        System.out.println("\n Number of variables: " + bdd.getNumVariables());
        System.out.println("\n Number of nodes: " + bdd.getSize());
        System.out.println("\n Compression rate: " + BDDUtl.getCompressRate(bdd.getNumVariables(), bdd.getSize()) );
        //bdd.showBDD(variableOrder);
*/
        //String [] val = {"0000", "0001", "0010", "0011","0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
/*
        for (int i = 0; i < 100; i++) {
            String st = BDDUtl.decToBin(i, variableOrder.length());
            System.out.println( st + " | " +BDDUtl.BDD_use(st, bdd.graph));
        }
*/
/*
        bdd = BDDUtl.BDD_create_with_best_order(dnf, 100);
        System.out.println("\n Number of variables for best order: " + bdd.getNumVariables());
        System.out.println("\n Number of nodes for best order: " + bdd.getSize());
        System.out.println("\n Compression rate: " + BDDUtl.getCompressRate(bdd.getNumVariables(), bdd.getSize()) );
*/

    }
}