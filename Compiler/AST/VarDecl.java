package Compiler.AST;

public class VarDecl{

  private String type;
  private IdList il;

  public VarDecl(String type, IdList il){
    this.type = type;
    this.il = il;
  }

  public void genC(PW pw){
    pw.print(this.type.toLower()+" ");
    this.il.genC();
    pw.println(";");
  }

  public boolean exists(Ident x){
    return il.exits(x);
  }

  public String getType(){
    return this.type;
  }
}