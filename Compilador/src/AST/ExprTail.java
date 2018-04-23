package AST;

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
