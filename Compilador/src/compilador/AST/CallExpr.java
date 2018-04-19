package compilador.AST;

public class CallExpr extends Expr{
    private Ident id;
    private ExprList el;
     
    public CallExpr(Ident i, ExprList e){
        this.id = id;
        this.el = e;
    }

    public void setId(Ident id) {
        this.id = id;
    }

    public void setEl(ExprList el) {
        this.el = el;
    }
    
    public void genC(){
       PW pw = PW.getPW();
       pw.print(this.id.getId());
       pw.print("(");
       this.el.genC();
       pw.print(")");
    }
}
    
