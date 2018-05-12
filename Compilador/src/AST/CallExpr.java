package AST;

import Semantic.SymbolTable;

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
    
    public String getCallExprType(SymbolTable s){
        return s.getFunction(this.id.getId());
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
    
