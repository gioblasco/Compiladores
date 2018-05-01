package AST;

public class FuncBody{
  private Declaration decl;
  private StmtList sl;

  public FuncBody(Declaration d, StmtList s){
    this.decl = d;
    this.sl = s;
  }
  
  public StmtList getStmtList(){
      return this.sl;
  }

  public void genC(){
    PW pw = PW.getPW();
    this.decl.genC();
    this.sl.genC();
  }
}
