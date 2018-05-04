package AST;

public class ReturnStmt extends Stmt{
    private Expr ex;    
    public ReturnStmt(Expr e){
     this.ex = e;   
    }
    
    public void genC(){
        PW pw = PW.getPW();
        pw.print("return ");
        this.ex.genC();
        pw.rawPrint(";\n");
    }
}
