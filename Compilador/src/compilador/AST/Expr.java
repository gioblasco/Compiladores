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
public class Expr {
    private Factor factor;
    private ExprTail et;
    
    
    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public void setExprTail(ExprTail et) {
        this.et = et;
    }

    public Factor getFactor() {
        return factor;
    }
    
    
    
    public Expr(){
        
    }

    public void genC() {
        
    }

  
    
}
