package AST;
    // factor_tail -> mulop postfix_expr factor_tail | empty
import Semantic.SymbolTable;
public class FactorTail extends Factor {
    
    private Character mulop;
    private PostfixExpr p;
    private FactorTail f;
    
    // factor_tail -> mulop postfix_expr factor_tail | empty
    public FactorTail(Character mulop, PostfixExpr p, FactorTail f)  {
        this.mulop = mulop;
        this.p = p;
        this.f = f;
    }

    public FactorTail() {
    }

    
    
    public void setMulop(Character mulop) {
        this.mulop = mulop;
    }

    public void setPostfixExpr(PostfixExpr p) {
        this.p = p;
    }

    public void setFactorTail(FactorTail f) {
        this.f = f;
    }

    public PostfixExpr getPostfixExpr() {
        return p;
    }

    public FactorTail getFactorTail() {
        return f;
    }
    
    public String getType(SymbolTable s){
        String typeofpe, typeoftail=null;
        typeofpe = this.p.getType(s);
        if(this.f != null && this.f.getPostfixExpr() != null)
            typeoftail = this.f.getType(s);
        if( (typeoftail != null && typeoftail.toLowerCase().equals("float") ) || typeofpe.toLowerCase().equals("float") )
            return "FLOAT";
        return "INT";
        
    }    
 
    public void genC(){
        PW pw = PW.getPW();
        if(this.mulop != null){
            pw.rawPrint(" "+this.mulop.toString()+" ");
            this.p.genC();
            this.f.genC();
        }        
    }

}
