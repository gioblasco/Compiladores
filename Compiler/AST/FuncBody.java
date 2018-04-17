package Compiler.AST;

public class FuncBody{
  private Decl decl;
  private StmtList sl;

  public FuncBody(Decl d, StmtList s){
    this.decl = d;
    this.sl = s;
  }

  public void genC(PW pw){
    this.decl.genC();
    this.sl.genC();
  }
}
