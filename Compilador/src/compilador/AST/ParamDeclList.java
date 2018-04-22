package compilador.AST;

import java.util.*;

public class ParamDeclList{
  private ArrayList<ParamDecl> alpd;
  private int i;

  public ParamDeclList(ArrayList<ParamDecl> al){
    this.alpd = al;
  }
  public void genC(){
    PW pw = PW.getPW();
    if(this.alpd.size()>0){
      for(i = 0; i < this.alpd.size(); i++){
        this.alpd.get(i).genC();
        if(i != this.alpd.size()-1)
            pw.rawPrint(", ");
      }
    }
  }

}
