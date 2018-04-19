package compilador.AST;

public class VarDecl{

  private String type;
  private IdList il;

  public VarDecl(String type, IdList il){
    this.type = type;
    this.il = il;
  }

  public void genC(PW pw){
    pw.print(this.type.toLowerCase()+" ");
    this.il.genC(pw);
    pw.println(";");
  }

  public boolean exists(Ident x){
    return il.exits(x);
  }

  public String getType(){
    return this.type;
  }
}
