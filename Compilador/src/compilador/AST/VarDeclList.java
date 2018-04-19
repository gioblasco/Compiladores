package compilador.AST;

import java.util.*;

public class VarDeclList/* extends DeclList*/{
  private ArrayList<VarDecl> dl;

  
  public void addDecl(VarDecl vd){
      this.dl.add(vd);
  }

  public void genC(){
    for(VarDecl x: this.dl){
      x.genC();
    }
  }

  public boolean exists(Ident arg){
    for(VarDecl x: this.dl)
      if(x.exists(arg))
        return true;
    return false;
  }

  public String getType(Ident arg){
    String retorno;
    for(VarDecl x: this.dl)
      if(x.exists(arg))
        return x.getType();
    return "";
  }

}
