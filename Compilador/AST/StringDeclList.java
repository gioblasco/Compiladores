public class StringDeclList {
  private ArrayList<StringDecl> sd;

  public addDecl(StringDecl strdecl){
    sd.add(strdecl);
  }

  public genC(){
    for(StringDecl x: thid.sd){
      x.genC();
    }
  }
}
