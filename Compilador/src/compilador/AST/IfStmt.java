package compilador.AST;

public class IfStmt extends Stmt{
    private Cond c;
    private StmtList ifpart, elsepart;
    
    public IfStmt(Cond c, StmtList i, StmtList e){
        this.c = c;
        this.ifpart = i;
        this.elsepart = e;
    }

    @Override
    public void genC() {
        PW pw = PW.getPW();
        pw.print("if(");
        this.c.genC();
        pw.println(")");
        pw.println("{");
        pw.add();
        if(this.ifpart != null){
            this.ifpart.genC();
        }
        pw.sub();
        if(this.elsepart != null){
           pw.println("}");
           pw.println("else");
           pw.println("{");
           pw.add();
           this.elsepart.genC();
           pw.sub();
           pw.println("}");
        }
        pw.println("}");
    }
}
