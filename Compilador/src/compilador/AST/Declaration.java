package compilador.AST;

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

  public boolean exists(Ident arg){
    return this.sd.exists(arg) || this.vd.exists(arg);
  }

  public void genC(PW pw){
    sd.genC(pw);
    vd.genC(pw);
  }
  
  public StringDeclList getSd(){
      return this.sd;
  }
  
  public VarDeclList getVd(){
      return this.vd;
  }
  
}
