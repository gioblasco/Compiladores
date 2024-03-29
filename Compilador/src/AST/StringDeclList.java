package AST;

import java.util.*;

public class StringDeclList /*extends DeclList*/{
  private ArrayList<StringDecl> dl;
  
  public StringDeclList(){
      this.dl = new ArrayList<StringDecl>();
  }

  public void addDecl(StringDecl strdecl){
    this.dl.add(strdecl);
  }

  public void genC(){
    for(StringDecl x: this.dl){
      x.genC();
    }
  }

  public boolean exists(Ident arg){
    for(StringDecl x: this.dl)
      if(x.exists(arg))
        return true;
    return false;
  }

}
