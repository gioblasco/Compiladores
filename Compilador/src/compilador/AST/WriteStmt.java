/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.AST;

/**
 *
 * @author giova
 */
public class WriteStmt extends Stmt{
    private IdList il;
    
    public WriteStmt(IdList il){
        this.il = il;
    }
    
    public void genC(PW pw){
        
    }
}
