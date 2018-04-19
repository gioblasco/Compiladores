package compilador.AST;

public class CallExpr {
    private Ident id;

    public void setId(Ident id) {
        this.id = id;
    }

    public void setEl(ExprList el) {
        this.el = el;
    }
    private ExprList el;
    public CallExpr(Ident i, ExprList e){
        this.id = id;
        this.el = e;
    }
    
    public void genC(PW pw){
       pw.print(this.id.getId());
       pw.print("(");
       this.el.genC();
       pw.print(");");
    }
}
