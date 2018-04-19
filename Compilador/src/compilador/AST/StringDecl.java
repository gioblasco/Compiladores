package compilador.AST;

public class StringDecl{

  private Ident id;
  private String string;

  public StringDecl(Ident id, String string){
    this.id = id;
    this.string = string;
  }

  public void genC(PW pw){
    pw.println("char "+this.id.getId()+"[] = \""+this.string+"\";");
  }

  public boolean exists(Ident arg){
    if(this.id.equals(arg))
      return true;
    return false;
  }
}
