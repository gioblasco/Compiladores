public class VarDeclList{
  private ArrayList<VarDecl> vd;

  public addDecl(VarDecl vd){
      this.vd = vd;
  }

  public genC(){
    for(VarDecl x: this.vd){
      x.genC();
    }
  }

}
