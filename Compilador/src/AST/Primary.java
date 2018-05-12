package AST;

import Semantic.SymbolTable;


public class Primary {
    private Expr e;
    private Ident i;
    private String literal;
    private String literaltype;

    public Primary(Expr e) {
        this.e = e;
    }

    public Primary(Ident i) {
        this.i = i;
    }

    public Primary(String literal, String literaltype) {
        this.literal = literal;
        this.literaltype = literaltype;
    }
   
    public String getType(SymbolTable s){
        if(this.i != null)
            return s.getVariable(this.i.getId());
        else if(this.e != null){
            return this.e.getType(s);
        }
        else if(this.literal != null){
            return this.literaltype;
        }
        return null;
        //debug here
    }
    
    public void setType(String type){
        this.literaltype = type;
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
