package AST;

public class AssignStmt extends Stmt{
  private AssignExpr alae;

  public AssignStmt(AssignExpr a){
    this.alae = a;
  }
  
  public AssignExpr getAssignExpr(){
    return this.alae;
  }
  
  public void genC(){
      PW pw = PW.getPW();
      this.alae.genC();
      pw.rawPrint(";\n");
  }
}
