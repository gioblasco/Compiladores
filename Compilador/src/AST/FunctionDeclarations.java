package AST;

import java.util.*;

public class FunctionDeclarations{
  private ArrayList<FuncDecl> alfd;

  public FunctionDeclarations(ArrayList<FuncDecl> al){
    this.alfd = al;
  }

  public void genC(){
    for(FuncDecl x: this.alfd){
      x.genC();
    }
  }
}
