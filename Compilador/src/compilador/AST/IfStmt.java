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
public class IfStmt extends Stmt{
    private Cond c;
    private StmtList ifpart, elsepart;
    
    public IfStmt(Cond c, StmtList ifpart, StmtList elsepar){
        
    }

    @Override
    public void genC() {
        
    }
}
