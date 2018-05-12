package AST;

import Semantic.SymbolTable;

public class Expr {
    private Factor factor;
    private ExprTail et;
    
    
    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public void setExprTail(ExprTail et) {
        this.et = et;
    }

    public Factor getFactor() {
        return factor;
    }
    
    
    
    public Expr(){
      
    }

    public ExprTail getExprTail() {
        return et;
    }
    
    
    

    public void genC() {
        this.factor.genC();
        if(this.et != null)
            this.et.genC();
    }

    public String getType(SymbolTable s) {
        String typeoffactor, typeoftail = null;
        typeoffactor = this.factor.getType(s);
        if(typeoftail != null && this.et.getType(s).toLowerCase().equals("float") || typeoffactor.toLowerCase().equals("float"))
            return "FLOAT";
        return "INT";
    }

  
    
}
