package AST;

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
        
        // if part...
        pw.print("if(");
        this.c.genC();
        pw.rawPrint(")\n");
        if(this.ifpart!= null && this.ifpart.size() > 1 || this.ifpart == null)
            pw.println("{");
        pw.add();
        if(this.ifpart != null){
            this.ifpart.genC();
        }
        pw.sub();
        if(this.ifpart!= null && this.ifpart.size() > 1 || this.ifpart == null)
            pw.println("}");
        
        // Else part...
        if(this.elsepart != null){
           pw.println("else");
           if(this.elsepart.size()>1)
            pw.println("{");
           pw.add();
           this.elsepart.genC();
           pw.sub();
           if(this.elsepart.size()>1)
            pw.println("}");
        }
        
    }
}
