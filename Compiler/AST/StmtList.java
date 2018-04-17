package Compiler.AST;

public class StmtList{
  private ArrayList<Stmt> als;

  public StmtList(ArrayList<Stmt> a){
    this.als = a;
  }

  public void genC(PW pw){
    for(Stmt x: this.als){
      x.genC();
    }
  }
}
