package compilador.AST;

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
    public void genC() {
        PW pw = PW.getPW();
        pw.print("for( ");
        init.genC();
        pw.rawPrint(" ; ");
        cond.genC();
        pw.rawPrint(" ; ");
        loop.genC();
        pw.rawPrint(" )\n");
        if(this.sl.size() > 1)
            pw.println("{");
        pw.add();
        this.sl.genC();
        pw.sub();
        if(this.sl.size() > 1)
            pw.println("}");
        
    }
    
    
}
