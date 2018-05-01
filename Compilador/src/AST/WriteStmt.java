/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AST;

import Error.CompilerError;
import Semantic.SymbolTable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author giova
 */
public class WriteStmt extends Stmt{
    private IdList il;
    private ArrayList<String> al;
    private HashMap<String,String> sufixes;
  
    
    public WriteStmt(IdList il, SymbolTable symbolTable, CompilerError error) {
        this.il = il;
        this.al = new ArrayList<String>();
        this.sufixes = new HashMap<String,String>();
        this.sufixes.put("STRING", "%s");
        this.sufixes.put("INT", "%d");
        this.sufixes.put("FLOAT", "%f");
        
        ArrayList<Ident> id = this.il.getIdList();
        for(Ident d : id){
            String result = symbolTable.get(d.getId());
            if (result == null)
                error.signal("Trying to execute a read stmt with a undeclared variable!");
            this.al.add(result);
        }
    }
    
    public void genC(){
        PW pw = PW.getPW();
        pw.print("printf(\"");
        for(int i = 0; i < this.il.size(); i++){
            pw.rawPrint(this.sufixes.get(this.al.get(i)));
            if(i < this.il.size()-1)
                pw.rawPrint(" ");
        }
        pw.rawPrint("\", ");
        this.il.genC();
        pw.rawPrint(");\n");
    }
}
