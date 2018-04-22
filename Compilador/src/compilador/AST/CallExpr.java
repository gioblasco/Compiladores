package compilador.AST;

public class CallExpr extends Expr{
    private Ident id;
    private ExprList el;
     
    public CallExpr(Ident id, ExprList e){
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
       pw.rawPrint(this.id.getId());
       pw.rawPrint("(");
       if(this.el != null)
        this.el.genC();
       pw.rawPrint(")");
    }
}
    
