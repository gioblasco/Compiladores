package AST;

import java.util.ArrayList;

public class ExprList {
    private ArrayList<Expr> ale;
    
    public ExprList(ArrayList<Expr> ale){
        this.ale = ale;
    }
    public ExprList(){
        this.ale = new ArrayList<Expr>();
    }
    
    public ArrayList<Expr> getExprList(){
        return this.ale;
    }
    
    public void genC(){
        PW pw = PW.getPW();
        for(int i = 0; i < this.ale.size(); i++){
            this.ale.get(i).genC();
            if(i != this.ale.size()-1)
                pw.rawPrint(", ");
        } 
    }
    
    public void add(Expr e){
        this.ale.add(e);
    }
}
