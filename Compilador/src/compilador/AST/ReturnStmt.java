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
public class ReturnStmt extends Stmt{
    private Expr ex;    
    public ReturnStmt(Expr e){
     this.ex = e;   
    }
    
    public void genC(){
        PW pw = PW.getPW();
        pw.print("return ");
        this.ex.genC();
        pw.rawPrint(";\n");
    }
}
