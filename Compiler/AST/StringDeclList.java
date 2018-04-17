package Compiler.AST;

public class StringDeclList /*extends DeclList*/{
  private ArrayList<StringDecl> dl;

  public void addDecl(StringDecl strdecl){
    sd.add(strdecl);
  }

  public void genC(PW pw){
    for(StringDecl x: thid.sd){
      x.genC();
    }
  }

  public boolean exists(Ident arg){
    for(StringDecl x: thid.sd)
      if(x.exists(arg))
        return true;
    return false;
  }

}
