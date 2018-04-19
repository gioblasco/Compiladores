package compilador.AST;

public class Factor {

    private PostfixExpr p;
    private FactorTail ft;

    public Factor(PostfixExpr p, FactorTail ft) {
        this.p = p;
        this.ft = ft;
    }

    public Factor() {
    }

    public PostfixExpr getPostfixExpr() {
        return p;
    }

    public FactorTail getFactorTail() {
        return ft;
    }

    public void setPostfixExpr(PostfixExpr p) {
        this.p = p;
    }

    public void setFactorTail(FactorTail ft) {
        this.ft = ft;
    }
    
    public void genC(){
        p.genC();
        if(ft != null)
            ft.genC();    
    }
    
    
}
