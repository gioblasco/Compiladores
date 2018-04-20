package compilador.AST;

public class ReadStmt extends Stmt{
  private IdList il;

  public ReadStmt(IdList il){
    this.il = il;
  }

  //não sei como saber os tipos de variaveis do id_list pra colocar no scanf
  public void genC(){
    PW pw = PW.getPW();
    pw.print("//scanf(\"");
    for(int i = 0; i < this.il.size(); i++){
        if(i != this.il.size()-1)
            pw.rawPrint("%t ");
        else
            pw.rawPrint("%t\", ");
    }
    this.il.genC();
    pw.rawPrint(");\n");
  }
}
