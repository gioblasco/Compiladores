package Compiler.AST;

public class StringDecl{

  private Ident id;
  private VariableString string;

  public StringDecl(Ident id, VariableString string){
    this.id = id;
    this.string = string;
  }

  public void genC(PW pw){
    pw.println("char "+this.id.getId()+"[] = \""+this.string.getVariableString()+"\";");
  }

  public boolean exists(Ident arg){
    if(this.id.equals(arg))
      return true;
    return false;
  }
}