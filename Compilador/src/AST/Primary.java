package AST;


public class Primary {
    private Expr e;
    private Ident i;
    private String literal;

    public Primary(Expr e) {
        this.e = e;
    }

    public Primary(Ident i) {
        this.i = i;
    }

    public Primary(String literal) {
        this.literal = literal;
    }
   
    
    public void genC() {
        PW pw = PW.getPW();
        if(this.e != null){
            pw.rawPrint("(");
            this.e.genC();
            pw.rawPrint(")");
        
        } else if(i!=null){
            pw.rawPrint(this.i.getId());
        } else{
            pw.rawPrint(this.literal);
        }
    }
    
}
