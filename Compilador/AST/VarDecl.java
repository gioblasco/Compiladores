public class VarDecl{

  private String type;
  private ArrayList<IdList> il;

  public VarDecl(String type, ArrayList<IdList> il){
    this.type = type;
    this.il = il;
  }

  public genC(){
    pw.print(type.toLower()+" ");
    if(this.il.size()>0){
      for(int i = 0; i < (this.il.size() - 1); i++){
        pw.print(this.il[i].getId()+", ")
      }
      pw.println(this.il[i+1].getId()+";")
    }
}
