package AST;

public class ParamDecl{
  private String type;
  private Ident id;

  public ParamDecl(String t, Ident i){
    this.type = t;
    this.id = i;
  }
  public void genC(){
    PW pw = PW.getPW();
    pw.print(this.type.toLowerCase()+" "+this.id.getName());
  }

  

    public String getType() {
        return type;
    }

    public Ident getId() {
        return id;
    }
  
  
  
}
