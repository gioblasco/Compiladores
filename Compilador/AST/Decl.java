public class Decl{
  private StringDeclList sd;
  private VarDeclList vd;

  public Decl(){
    this.sd = null;
    this.vd = null;
  }

  public setStringDeclList(StringDeclList sd){
    this.sd = sd;
  }

  public setVarDeclList(VarDeclList vd){
    this.vd = vd;
  }

  public genC(){
    sd.genC();
    vd.genC();
  }
}
