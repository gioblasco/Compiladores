package compilador.AST;

public class ReadStmt extends Stmt{
  private IdList il;

  public ReadStmt(IdList il){
    this.il = il;
  }

  //não sei como saber os tipos de variaveis do id_list pra colocar no scanf
  public void genC(PW pw){
    pw.print("scanf(");
  }
}
