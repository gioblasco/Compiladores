package compilador.AST;


public class PostfixExpr {
    private Primary p;
    private CallExpr c;

    public PostfixExpr(Primary p) {
        this.p = p;
    }

    public PostfixExpr(CallExpr c) {
        this.c = c;
    }

    public PostfixExpr() {
    }

    public void setPrimary(Primary p) {
        this.p = p;
    }

    public void setCallExpr(CallExpr c) {
        this.c = c;
    }
    
    
    
    
    public void genC() {
        PW pw = PW.getPW();
        if(p != null)
            p.genC();
        else
            c.genC();
    }
}
