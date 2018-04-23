package AST;

public class AssignExpr extends Expr{
  private Ident id;
  private Expr expr;

  public AssignExpr(Ident i, Expr e){
    this.id = i;
    this.expr = e;
  }

  public void genC(){
    PW pw = PW.getPW();
    pw.print(this.id.getId()+" = ");
    this.expr.genC();
  }
}
