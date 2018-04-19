package compilador.AST;

public class AssignExpr{
  private Ident id;
  private Expr expr;

  public AssignExpr(Ident i, Expr e){
    this.id = i;
    this.expr = e;
  }

  public void genC(PW pw){
    pw.print(this.id.getId()+" = ");
    this.expr.genC(pw);
  }
}
