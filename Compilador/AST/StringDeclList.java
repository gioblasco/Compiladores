public class StringDeclList {
  private ArrayList<StringDecl> sd;

  public void addDecl(StringDecl strdecl){
    sd.add(strdecl);
  }

  public void genC(PW pw){
    for(StringDecl x: thid.sd){
      x.genC();
    }
  }
}
