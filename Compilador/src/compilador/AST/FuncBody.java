package compilador.AST;

public class FuncBody{
  private Declaration decl;
  private StmtList sl;

  public FuncBody(Declaration d, StmtList s){
    this.decl = d;
    this.sl = s;
  }

  public void genC(){
    PW pw = PW.getPW();
    this.decl.genC();
    this.sl.genC();
  }
}
