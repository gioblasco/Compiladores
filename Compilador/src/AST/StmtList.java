package AST;

import java.util.*;

public class StmtList {

    private ArrayList<Stmt> als;

    public StmtList(ArrayList<Stmt> a) {
        this.als = a;
    }

    public void genC() {
        if (this.als != null) {
            for (Stmt x : this.als) {
                x.genC();
            }
        }
    }

    public int size() {
        return this.als.size();
    }
}