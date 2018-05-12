package AST;

public class StringDecl{

  private Ident id;
  private String string;

  public StringDecl(Ident id, String string){
    this.id = id;
    this.string = string;
  }

  public void genC(){
    PW pw = PW.getPW();
    pw.println("char "+this.id.getName()+"[] = \""+this.string+"\";");
  }

  public boolean exists(Ident arg){
    if(this.id.equals(arg))
      return true;
    return false;
  }
}
