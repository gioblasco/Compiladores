package compilador.AST;
    // factor_tail -> mulop postfix_expr factor_tail | empty
public class FactorTail {
    
    private Character mulop;
    private PostfixExpr p;
    private FactorTail f;
    
    // factor_tail -> mulop postfix_expr factor_tail | empty
    public FactorTail(Character mulop, PostfixExpr p, FactorTail f) {
        this.mulop = mulop;
        this.p = p;
        this.f = f;
    }

    public FactorTail() {
    }

    
    
    public void setMulop(Character mulop) {
        this.mulop = mulop;
    }

    public void setPostfixExpr(PostfixExpr p) {
        this.p = p;
    }

    public void setFactorTail(FactorTail f) {
        this.f = f;
    }

    public PostfixExpr getPostfixExpr() {
        return p;
    }

    public FactorTail getFactorTail() {
        return f;
    }
    
    
 
    public void genC(){
        PW pw = PW.getPW();
        if(this.mulop != null){
            pw.print(this.mulop.toString());
            this.p.genC();
            this.f.genC();
        }        
    }

}
