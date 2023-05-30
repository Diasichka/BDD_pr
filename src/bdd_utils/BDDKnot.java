package bdd_utils;

import java.util.Map;

public class BDDKnot {
    private int variable;
    public int index;
    public int parent;
    private int low;
    private int high;
    private int  lTerm;
    private int rTerm;

    public BDDKnot(int variable, int low, int high) {
        this.variable = variable;
        this.parent = -1;
        this.index = -1;
        this.low = low;
        this.high = high;
        this.lTerm = -1;
        this.rTerm = -1;
    }

    public int getVariable() {
        return variable;
    }
    public int getLTerm() {
        return lTerm;
    }
    public int getRTerm() {
        return rTerm;
    }
    public void setLTerm(int val) {
        this.lTerm = val;
    }
    public void setRTerm(int val) {
        this.rTerm = val;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public void setHigh(int high) {
        this.high = high;
    }
}