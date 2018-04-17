public class Declaration{
  private StringDeclList sd;
  private VarDeclList vd;

  public Declaration(){
    this.sd = null;
    this.vd = null;
  }

  public void setStringDeclList(StringDeclList sd){
    this.sd = sd;
  }

  public void setVarDeclList(VarDeclList vd){
    this.vd = vd;
  }

  public void genC(PW pw){
    sd.genC();
    vd.genC();
  }
}
