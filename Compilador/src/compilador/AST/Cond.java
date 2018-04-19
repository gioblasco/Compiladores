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
public class Cond {

    private Expr exp1, exp2;
    private Character op;
    
    public void setExpr1(Expr exp1) {
        this.exp1 = exp1;
    }

    public void setExpr2(Expr exp2) {
        this.exp2 = exp2;
    }

    public void setOp(Character op) {
        this.op = op;
    }
  
    

}
