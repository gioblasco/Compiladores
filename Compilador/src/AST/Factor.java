package AST;

import Semantic.SymbolTable;

public class Factor {

    private PostfixExpr p;
    private FactorTail ft;

    public Factor(PostfixExpr p, FactorTail ft) {
        this.p = p;
        this.ft = ft;
    }

    public Factor() {
    }

    public PostfixExpr getPostfixExpr() {
        return p;
    }

    public FactorTail getFactorTail() {
        return ft;
    }

    public void setPostfixExpr(PostfixExpr p) {
        this.p = p;
    }

    public void setFactorTail(FactorTail ft) {
        this.ft = ft;
    }
    
    public String getType(SymbolTable s) {
        String typeofpe, typeoftail = null;
        typeofpe = this.p.getType(s);
        if(this.ft != null)
            typeoftail = this.ft.getType(s);
        if(typeofpe.toLowerCase().equals("float") || (typeoftail != null && typeoftail.toLowerCase().equals("float")))
            return "FLOAT";
        return "INT";
    }
    public void genC(){
        p.genC();
        if(ft != null)
            ft.genC();    
    }
    
    
}
