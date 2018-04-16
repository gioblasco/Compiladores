public class ParamDeclList{
  private ArrayList<ParamDecl> alpd;

  public ParamDeclList(ArrayList<ParamDecl> al){
    this.alpd = al;
  }
  public genC(){
    if(this.alpd.size()>0){
      for(int i = 0; i < (this.alpd.size() - 1); i++){
        this.alpd[i].genC();
        pw.print(", ")
      }
      this.alpd[i+1].genC();
    }
  }

}
