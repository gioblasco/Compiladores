package Compiler.AST;

public class AssignStmt extends Stmt{
  private ArrayList<AssignExpr> alae;

  public AssignStmt(ArrayList<AssignExpr> a){
    this.alae = a;
  }

  public void genC(PW pw){
    for(AssignExpr x: this.alae){
      x.genC();
    }
    pw.println(";");
  }
}
