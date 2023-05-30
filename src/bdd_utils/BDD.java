package bdd_utils;

import java.util.HashMap;
import java.util.Map;

public class BDD {
    private int numVariables;
    private int size;
    private int root;
    public Map<Integer, BDDKnot> graph;

    public BDD(int numVariables) {
        this.numVariables = numVariables;

        graph = new HashMap<>();
    }

    public int getNumVariables() {
        return numVariables;
    }

    public int getSize() {
        return graph.size();
    }

    public int addKnot(int idx, int parent, int value, Integer low, Integer high) {
        //int node = graph.size();
        BDDKnot knot = new BDDKnot(value, low, high);
        knot.index = idx;
        knot.parent = parent;
        graph.put(idx, knot);
        return idx;
    }

    public int getRoot() {
        return root;
    }

    public void showBDD(String varOrder){
        char[] variableOrder = varOrder.strip().toCharArray();
        BDDKnot knot = null;
        String st = "", buf = "";
        for(int i  =0; i < numVariables; i++) {
            st = "";
            buf = "   ";
            //for (int z = 0; z < 80 - 10 * i; z++) st += " ";
            //for (int z = 0; z < 20 - (7-i) * i; z++) buf += " ";

            for (int j = 0; j < Math.pow(2, i); j++){
                knot = graph.get((int) Math.pow(2, i) - 1 + j);
                if (knot != null) {
                    st += "{(" + variableOrder[i] + ")";
                    if (knot.getLow() != -1) st += " / ";
                    else st += " [" + knot.getLTerm() + "]";
                    if (knot.getHigh() != -1) st += " \\ }";
                    else st += " [" + knot.getRTerm() + "] }";
                } else st += "__";
                st += buf;
            }
            System.out.println(st);
        }

    }
}



