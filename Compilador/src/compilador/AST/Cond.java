package compilador.AST;

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
  
    public void genC(){
        PW pw = PW.getPW();
        this.exp1.genC();
        pw.rawPrint(" "+this.op.toString()+" ");
        this.exp2.genC();
    }
    

}
