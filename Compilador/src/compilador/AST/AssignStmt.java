package compilador.AST;

public class AssignStmt extends Stmt{
  private AssignExpr alae;

  public AssignStmt(AssignExpr a){
    this.alae = a;
  }

  public void genC(){
      PW pw = PW.getPW();
      this.alae.genC();
      pw.println(";");
  }
}
