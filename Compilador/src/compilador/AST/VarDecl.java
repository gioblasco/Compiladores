package compilador.AST;

public class VarDecl{

  private String type;
  private IdList il;

  public VarDecl(String type, IdList il){
    this.type = type;
    this.il = il;
  }

  public void genC(){
    PW pw = PW.getPW();
    pw.print(this.type.toLowerCase()+" ");
    this.il.genC();
    pw.rawPrint( ";\n");
  }

  public boolean exists(Ident x){
    return il.exists(x);
  }

  public String getType(){
    return this.type;
  }
}
