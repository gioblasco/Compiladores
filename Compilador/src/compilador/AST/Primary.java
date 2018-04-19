package compilador.AST;


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
        if(e != null){
            pw.print("(");
            this.e.genC();
            pw.print(")");
        
        } else if(i!=null){
            pw.print(this.i.getId());
        } else{
            pw.print(this.literal);
        }
    }
    
}
