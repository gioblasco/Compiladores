package compilador.AST;

import java.util.*;

public class StmtList{
  private ArrayList<Stmt> als;

  public StmtList(ArrayList<Stmt> a){
    this.als = a;
  }

  public void genC(){
    for(Stmt x: this.als){
      x.genC();
    }
  }
}
