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
public class ForStmt extends Stmt {

    private StmtList sl;
    private Cond cond;
    private AssignExpr init,loop;
    
    public ForStmt(){
        
    }
    
    public void setSl(StmtList sl) {
        this.sl = sl;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public void setInit(AssignExpr init) {
        this.init = init;
    }

    public void setLoop(AssignExpr loop) {
        this.loop = loop;
    }
   
    
    @Override
    public void genC(PW pw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
