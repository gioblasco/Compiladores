package AST;

import Error.CompilerError;
import Semantic.SymbolTable;
import java.util.ArrayList;
import java.util.HashMap;


public class ReadStmt extends Stmt{
  private IdList il;
  private ArrayList<String> al;
  private HashMap<String,String> sufixes;
  
  

    public ReadStmt(IdList il, SymbolTable symbolTable, CompilerError error) {
        this.il = il;
        this.al = new ArrayList<String>();
        this.sufixes = new HashMap<String,String>();
        this.sufixes.put("INT", "%d");
        this.sufixes.put("FLOAT", "%f");
        
        ArrayList<Ident> id = this.il.getIdList();
        for(Ident d : id){
            String result = symbolTable.getVariable(d.getId());
            if (result == null)
                error.signal("Trying to execute a read stmt with a undeclared variable!");
            else if(result.toUpperCase().equals("STRING"))
                error.signal("Trying to read a String, String are imutable objects!");
            this.al.add(result);
        }
    }

  //não sei como saber os tipos de variaveis do id_list pra colocar no scanf
  public void genC(){
    PW pw = PW.getPW();
    pw.print("scanf(\"");
    for(int i = 0; i < this.il.size(); i++){
        pw.rawPrint(this.sufixes.get(this.al.get(i)));
        if(i < this.il.size()-1)
            pw.rawPrint(" ");
        
    }
    pw.rawPrint("\", &");
    this.il.genC();
    pw.rawPrint(");\n");
  }
}
