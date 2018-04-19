package compilador.AST;

import java.util.*;

public class IdList{
  private ArrayList<Ident> ali;
  private int i;

  public IdList(ArrayList<Ident> al){
    this.ali = al;
  }

  public void genC(){
    PW pw = PW.getPW();
    if(this.ali.size()>0){
      for(i = 0; i < (this.ali.size() - 1); i++){
        pw.print(this.ali.get(i).getId()+", ");
      }
      pw.print(this.ali.get(i+1).getId());
    }
  }

  public boolean exists(Ident x){
    if(this.ali.indexOf(x) != -1)
      return true;
    return false;
  }

}
