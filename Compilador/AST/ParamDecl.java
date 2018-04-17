public class ParamDecl{
  private String type;
  private Ident id;

  public ParamDecl(String t, Ident i){
    this.type = t;
    this.id = i;
  }
  public void genC(PW pw){
    pw.print(this.type+" "+this.id.getId());
  }
}
