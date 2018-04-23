/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AST;

/**
 *
 * @author giova
 */
public class WriteStmt extends Stmt{
    private IdList il;
    
    public WriteStmt(IdList il){
        this.il = il;
    }
    
    public void genC(){
        PW pw = PW.getPW();
        pw.print("//printf(\"");
        for(int i = 0; i < this.il.size(); i++){
            if(i != this.il.size()-1)
                pw.rawPrint("%t ");
            else
                pw.rawPrint("%t\", ");
        }
        this.il.genC();
        pw.rawPrint(");\n");
    }
}
