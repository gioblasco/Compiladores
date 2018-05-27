package AST;

import Semantic.SymbolTable;

public class ExprTail extends Expr{
    private Character addop;
    private Factor factor;
    private ExprTail exprtail;
    
    @Override
    public void genC() {
        
        PW pw = PW.getPW();
        pw.rawPrint(" "+this.addop.toString()+" ");
        this.factor.genC();
        if(this.exprtail!=null)
            this.exprtail.genC();   
    }

    @Override
    public String getType(SymbolTable s){
        String typeoffactor, typeoftail = null;
        typeoffactor = this.factor.getType(s);
        if(this.exprtail != null && this.exprtail.getAddop() != null)
            typeoftail = this.exprtail.getType(s);
        if(typeoffactor != null && typeoftail != null){
            if(typeoffactor.toLowerCase().equals("float") || (typeoftail.toLowerCase().equals("float")))
                return "FLOAT";
            else
                return "INT";
        } else if(typeoffactor != null)
            return typeoffactor.toUpperCase();
        else if(typeoftail != null)
            return typeoftail.toUpperCase();
        else
            return null;        
    }
    public void setAddop(Character addop) {
        this.addop = addop;
    }

    @Override
    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    @Override
    public void setExprTail(ExprTail exprtail) {
        this.exprtail = exprtail;
    }

    public Character getAddop() {
        return addop;
    }

    @Override
    public Factor getFactor() {
        return factor;
    }

    @Override
    public ExprTail getExprTail() {
        return exprtail;
    }
    
    
    
    
}
