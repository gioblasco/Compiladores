public class VarDeclList/* extends DeclList*/{
  private ArrayList<VarDecl> dl;

  
  public void addDecl(VarDecl vd){
      this.vd = vd;
  }

  public void genC(PW pw){
    for(VarDecl x: this.vd){
      x.genC();
    }
  }

  public boolean exists(Ident arg){
    for(VarDecl x: this.vd)
      if(x.exists(arg))
        return true;
    return false;
  }

  public String getType(Ident arg){
    String retorno;
    for(VarDecl x: this.vd)
      if(x.exists(arg))
        return x.getType();
    return "";
  }

}
