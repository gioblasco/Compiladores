public class IdList{
  private ArrayList<Ident> ali;

  public IdList(ArrayList<Ident> al){
    this.ali = al;
  }

  public genC(){
    if(this.ali.size()>0){
      for(int i = 0; i < (this.ali.size() - 1); i++){
        pw.print(this.ali[i].getId()+", ")
      }
      pw.print(this.ali[i+1].getId());
    }
  }
}
