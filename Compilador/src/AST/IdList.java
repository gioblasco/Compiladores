package AST;

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
      for(i = 0; i < this.ali.size(); i++){
        pw.rawPrint(this.ali.get(i).getId());
        if(i != (this.ali.size()-1))
            pw.rawPrint(", ");
      }
    }
  }
  
  public ArrayList<Ident> getIdList(){
      return ali;
  }

   public int size(){
       return this.ali.size();
   }
   
  public boolean exists(Ident x){
    if(this.ali.indexOf(x) != -1)
      return true;
    return false;
  }

}
