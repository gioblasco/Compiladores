public class VarDeclList{
  private ArrayList<VarDecl> vd;

  public void addDecl(VarDecl vd){
      this.vd = vd;
  }

  public void genC(PW pw){
    for(VarDecl x: this.vd){
      x.genC();
    }
  }

}
