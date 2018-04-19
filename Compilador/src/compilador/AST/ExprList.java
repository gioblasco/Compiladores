package compilador.AST;

import java.util.ArrayList;

public class ExprList {
    private ArrayList<Expr> ale;
    
    public ExprList(ArrayList<Expr> ale){
        this.ale = ale;
    }
    public ExprList(){
        this.ale = new ArrayList<Expr>();
    }
    public void genC(){
        System.out.println("sonoooo");
    }
    
}
