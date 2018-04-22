package compilador.AST;

public class CallStmt extends Stmt{
    private CallExpr ce;
    public CallStmt(CallExpr c){
        this.ce = c;
    }
    
    public void genC(){
        PW pw = PW.getPW();
        pw.print("");
        this.ce.genC();
        pw.print(";\n");
    }
    
}
