public class StringDecl{

  private Ident id;
  private VariableString string;

  public StringDecl(Ident id, VariableString string){
    this.id = id;
    this.string = string;
  }

  public genC(){
    pw.println("char "+this.id.getId()+"[] = \""+this.string.getVariableString()+"\";");
  }
}