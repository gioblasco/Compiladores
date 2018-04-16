public class VarDecl{

  private String type;
  private IdList il;

  public VarDecl(String type, IdList il){
    this.type = type;
    this.il = il;
  }

  public genC(){
    pw.print(this.type.toLower()+" ");
    this.il.genC();
    pw.println(";")
  }
}
