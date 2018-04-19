
import compilador.Lexer.*;
import compilador.Error.*;
import compilador.AST.*;

import java.util.*;
import java.io.*;

public class Compiler {

    // para geracao de codigo
    public static final boolean GC = false;

    public Program compile(char[] p_input) {
        error = new CompilerError(null);
        lexer = new Lexer(p_input, error);
        error.setLexer(lexer);
        lexer.nextToken();
        Program p = program();
        if (lexer.token != Symbol.EOF) {
            error.signal("Not expected ''" + lexer.token + "' after END keyword (end of file).'");
        }
        if (error.wasAnErrorSignalled()) {
            return null;
        }
        return p;
    }

    /**
     * ************************************
     */
    /**
     * ************ Program ***************
     */
    /**
     * ************************************
     */
    //   program ::= PROGRAM id BEGIN pgm_body END
    public Program program() {

        if (lexer.token != Symbol.PROGRAM) {
            error.signal("Missing PROGRAM keyword at program()");
        }

        lexer.nextToken();

        if (lexer.token != Symbol.IDENT) {
            error.signal("Missing PROGRAM Identifier at program()");
        }

        Ident id = new Ident(lexer.getStringValue());

        lexer.nextToken();

        if (lexer.token != Symbol.BEGIN) {
            error.signal("Missing BEGIN keyword at program()");
        }

        lexer.nextToken();

        ProgramBody pgm = pgm_body();

        if (lexer.token != Symbol.END) {
            error.signal("Missing END keyword at program()");
        }

        lexer.nextToken();

        return new Program(id, pgm);
    }

    // pgm_body -> decl func_declarations
    public ProgramBody pgm_body() {

        Declaration dec = decl(null);
        FunctionDeclarations func = func_declarations();

        return new ProgramBody(dec, func);
    }

    // decl -> string_decl_list {decl} | var_decl_list {decl} | empty
    public Declaration decl(Declaration d) {
        if (d == null) {
            d = new Declaration();
        }

        if (lexer.token == Symbol.STRING) {
            // devemos concatenar as strings
            string_decl_list(d.getSd());
            d.setStringDeclList(d.getSd());

            decl(d);
        } else if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            // devemos concatenar as variáveis
            var_decl_list(d.getVd());
            d.setVarDeclList(d.getVd());

            decl(d);
        }
        return d;
    }

    /**
     * **************************
     */
    /* Global String Declaration */
    /**
     * **************************
     */
    // string_decl_list -> string_decl {string_decl_tail}
    public void string_decl_list(StringDeclList sd) {
        if (sd == null) {
            sd = new StringDeclList();
        }
        string_decl(sd);
        if (lexer.token == Symbol.STRING) {
            string_decl_tail(sd);
        }
    }

    // string_decl -> STRING id := str ; | empty
    public void string_decl(StringDeclList sd) {
        if (lexer.token == Symbol.STRING) {

            if (lexer.nextToken() != Symbol.IDENT) {
                error.signal("Missing identifier in string declaration");
            }

            Ident id = new Ident(lexer.getStringValue());

            if (lexer.nextToken() != Symbol.ASSIGN) {
                error.signal("Missing assignment symbol");
            }

            lexer.nextToken();

            if (lexer.token != Symbol.STRINGLITERAL) {
                error.signal("Missing STRINGLITERAL type");
            }

            String str = lexer.getStringValue();

            sd.addDecl(new StringDecl(id, str));

            lexer.nextToken();

            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Error: Missing end of declaration");
            }

            lexer.nextToken();
        }
    }

    // string_decl_tail -> string_decl {string_decl_tail}
    public void string_decl_tail(StringDeclList sd) {
        string_decl(sd);
        if (lexer.token == Symbol.STRING) {
            string_decl_tail(sd);
        }
    }

    /**
     * *********************
     */
    /* Variable Declaration */
    /**
     * *********************
     */
    // var_decl_list -> var_decl {var_decl_tail}
    public void var_decl_list(VarDeclList vd) {
        if (vd == null) {
            vd = new VarDeclList();
        }
        var_decl(vd);
        if (lexer.token != Symbol.FLOAT && lexer.token == Symbol.INT) {
            var_decl_tail(vd);
        }
    }

    // var_decl -> var_type id_list ; | empty
    public void var_decl(VarDeclList vd) {
        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            String temp = lexer.getStringValue();
            lexer.nextToken();
            vd.addDecl(new VarDecl(temp, id_list()));
            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Missing end of declaration at var_decl()");
            }
            lexer.nextToken();
        }
    }

    // var_type -> FLOAT | INT
    public boolean var_type() {
        if (lexer.token != Symbol.FLOAT && lexer.token != Symbol.INT) {
            return false;
        }
        lexer.nextToken();
        return true;
    }

    // any_type -> var_type | VOID
    public void any_type() {
        if (var_type()) {
            return;
        }
        if (lexer.token != Symbol.VOID) {
            error.signal("Wrong type");
        }
        lexer.nextToken();
    }

    // id_list -> id id_tail
    public IdList id_list() {
        ArrayList<Ident> al = new ArrayList<Ident>();
        if (lexer.token == Symbol.IDENT) {
            al.add(new Ident(lexer.getStringValue()));
            lexer.nextToken();
            id_tail(al);
        } else {
            error.signal("Wrong id_list declaration");
        }
        return new IdList(al);
    }

    // id_tail -> , id id_tail | empty
    public void id_tail(ArrayList<Ident> al) {
        if (lexer.token == Symbol.COMMA) {
            lexer.nextToken();

            if (lexer.token != Symbol.IDENT) {
                error.signal("Missing identifier at id_tail()");
            }
            al.add(new Ident(lexer.getStringValue()));
            lexer.nextToken();

            id_tail(al);
        }
    }

    // var_decl_tail -> var_decl {var_decl_tail}
    public void var_decl_tail(VarDeclList vd) {
        var_decl(vd);
        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            var_decl_tail(vd);
        }
    }

    /**
     * ******************************
     */
    /**
     * ** Function Parameter List ***
     */
    /**
     * ******************************
     */
    // param_decl_list -> param_decl param_decl_tail
    public ParamDeclList param_decl_list() {
        ArrayList<ParamDecl> pd = new ArrayList<ParamDecl>();
        param_decl(pd);
        param_decl_tail(pd);

        return new ParamDeclList(pd);
    }

    // param_decl -> var_type id
    public void param_decl(ArrayList<ParamDecl> pd) {
        if (lexer.token != Symbol.FLOAT && lexer.token != Symbol.INT) {
            error.signal("Missing correct variable type at param_decl()");
        }

        String type = lexer.getStringValue();

        if (lexer.token != Symbol.IDENT) {
            error.signal("Missing identifier at param_decl()");
        }

        Ident id = new Ident(lexer.getStringValue());
        lexer.nextToken();

        pd.add(new ParamDecl(type, id));
    }

    // param_decl_tail -> , param_decl param_decl_tail | empty
    public void param_decl_tail(ArrayList<ParamDecl> pd) {
        if (lexer.token == Symbol.COMMA) {
            lexer.nextToken();

            param_decl(pd);
            param_decl_tail(pd);
        }
    }

    /**
     * ************************************
     */
    /**
     * ****** Function Declarations *******
     */
    /**
     * ************************************
     */
    // func_declarations -> func_decl {func_decl_tail}
    public FunctionDeclarations func_declarations() {
        ArrayList<FuncDecl> fd = new ArrayList<FuncDecl>();
        func_decl(fd);
        if (lexer.token == Symbol.FUNCTION) {
            func_decl_tail(fd);
        }
        return new FunctionDeclarations(fd);
    }

    // func_decl -> FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
    public void func_decl(ArrayList<FuncDecl> al) {
        ParamDeclList pdl = null;
        if (lexer.token == Symbol.FUNCTION) {
            lexer.nextToken();

            if (lexer.token != Symbol.FLOAT && lexer.token != Symbol.INT && lexer.token != Symbol.VOID) {
                error.signal("Missing function type");
            }

            String type = lexer.getStringValue();
            lexer.nextToken();

            if (lexer.token != Symbol.IDENT) {
                error.signal("Missing identifier at func_decl()");
            }

            Ident id = new Ident(lexer.getStringValue());
            lexer.nextToken();

            if (lexer.token != Symbol.LPAR) {
                error.signal("Missing parantheses at func_decl()");
            }
            lexer.nextToken();

            if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
                pdl = param_decl_list();
            }

            if (lexer.token != Symbol.RPAR) {
                error.signal("Missing parantheses at func_decl()");
            }
            lexer.nextToken();

            if (lexer.token != Symbol.BEGIN) {
                error.signal("Missing BEGIN keyword at func_decl()");
            }
            lexer.nextToken();

            FuncBody fb = func_body();

            if (lexer.token != Symbol.END) {
                error.signal("Missing END keyword at func_decl()");
            }
            lexer.nextToken();

            al.add(new FuncDecl(type, id, pdl, fb));
        }
    }

    // func_decl_tail -> func_decl {func_decl_tail}
    public void func_decl_tail(ArrayList<FuncDecl> al) {
        func_decl(al);
        if (lexer.token == Symbol.FUNCTION) {
            func_decl_tail(al);
        }
    }

    // func_body -> decl stmt_list
    public FuncBody func_body() {
        Declaration dec = decl(null);
        ArrayList<Stmt> alstmt = new ArrayList<Stmt>();
        stmt_list(alstmt);

        return new FuncBody(dec, new StmtList(alstmt));
    }

    /**
     * ***************
     */
    /* Statement List */
    /**
     * ***************
     */
    // stmt_list -> stmt stmt_tail | empty
    public void stmt_list(ArrayList<Stmt> alstmt) {
        if (lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR || lexer.token == Symbol.IDENT) {
            stmt(alstmt);
            stmt_tail(alstmt);
        }
    }

    // stmt_tail -> stmt stmt_tail | empty
    public void stmt_tail(ArrayList<Stmt> alstmt) {
        if (lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR || lexer.token == Symbol.IDENT) {
            stmt(alstmt);
            stmt_tail(alstmt);
        }
    }

    // stmt -> assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt | call_expr ;
    public void stmt(ArrayList<Stmt> alstmt) {
        if (lexer.token == Symbol.READ) {
            alstmt.add(read_stmt());
        } else if (lexer.token == Symbol.WRITE) {
            alstmt.add(write_stmt());
        } else if (lexer.token == Symbol.RETURN) {
            alstmt.add(return_stmt());
        } else if (lexer.token == Symbol.IF) {
            alstmt.add(if_stmt());
        } else if (lexer.token == Symbol.FOR) {
            alstmt.add(for_stmt());
        } else if (lexer.token == Symbol.IDENT) {
            Symbol temp = lexer.checkNextToken();
            if (temp == Symbol.ASSIGN) {
                alstmt.add(assign_stmt());
            } else if (temp == Symbol.LPAR) {
                alstmt.add(call_stmt());
                if (lexer.token != Symbol.SEMICOLON) {
                    error.signal("Missing semicolon after call_expr() at stmt()");
                }
                lexer.nextToken();
            } else {
                error.signal("Wrong use of element after identifier at stmt()");
            }
        } else {
            error.signal("Wrong statement declaration at stmt()");
        }
    }

    /**
     * *****************
     */
    /* Basic Statements */
    /**
     * *****************
     */
    // assign_stmt -> assign_expr ;
    public AssignStmt assign_stmt() {
        AssignExpr ae = assign_expr();
        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Semicolon expected at assign_stmt()" + lexer.getStringValue());
        }
        lexer.nextToken();

        return new AssignStmt(ae);
    }

    // assign_expr -> id := expr
    public AssignExpr assign_expr() {
        if (lexer.token != Symbol.IDENT) {
            error.signal("Expecting identifier at assign_expr()");
        }
        Ident id = new Ident(lexer.getStringValue());
        if (lexer.nextToken() != Symbol.ASSIGN) {
            error.signal("Expecting assign signal assign_expr()");
        }
        lexer.nextToken();
        return new AssignExpr(id, expr());
    }

    // read_stmt -> READ ( id_list );
    public ReadStmt read_stmt() {
        if (lexer.token != Symbol.READ) {
            error.signal("Missing READ keyword at read_stmt()");
        }
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Missing open parentheses at read_stmt()");
        }
        lexer.nextToken();
        IdList il = id_list();
        if (lexer.token != Symbol.RPAR) {
            error.signal("Missing close parentheses at read_stmt()");
        }
        if (lexer.nextToken() != Symbol.SEMICOLON) {
            error.signal("Semicolon expected at read_stmt()");
        }
        lexer.nextToken();
        return new ReadStmt(il);
    }

    // write_stmt -> WRITE ( id_list );
    public WriteStmt write_stmt() {
        if (lexer.token != Symbol.WRITE) {
            error.signal("Missing READ keyword at write_stmt()");
        }
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Missing open parentheses at write_stmt()");
        }
        lexer.nextToken();
        IdList il = id_list();
        if (lexer.token != Symbol.RPAR) {
            error.signal("Missing close parentheses write_stmt()");
        }
        if (lexer.nextToken() != Symbol.SEMICOLON) {
            error.signal("Semicolon expected at write_stmt()");
        }
        lexer.nextToken();
        return new WriteStmt(il);
    }

    // return_stmt -> RETURN expr ;
    public ReturnStmt return_stmt() {
        if (lexer.token != Symbol.RETURN) {
            error.signal("Missing RETURN keyword at return_stmt()");
        }
        lexer.nextToken();
        Expr e = expr();
        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Semicolon expected at return_stmt()");
        }
        lexer.nextToken();
        return new ReturnStmt(e);
    }

    /**
     * ************
     */
    /* Expressions */
    /**
     * ************
     */
    // expr -> factor expr_tail
    public Expr expr() {
        Expr e = new Expr();
        if (factor(e)) {
            expr_tail(e);
        } else {
            error.signal("Factor expected at expr()");
        }
        return e;
    }

    // expr_tail -> addop factor expr_tail | empty
    public boolean expr_tail(Expr e) {
        ExprTail et = null;
        if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS) {
            lexer.nextToken();
            if (factor(e)) {
                return expr_tail(e);
            } else {
                return false;
            }
        }
        return false;
    }

    // factor -> postfix_expr factor_tail
    public boolean factor(Expr e) {
        e.setFactor(new Factor());
        PostfixExpr pe = new PostfixExpr();
        e.getFactor().setFactorTail(new FactorTail());
        if (postfix_expr(pe)) {
            e.getFactor().setPostfixExpr(pe);
            return factor_tail(e.getFactor().getFactorTail());
        }
        
        return false;
    }

    // factor_tail -> mulop postfix_expr factor_tail | empty
    public boolean factor_tail(FactorTail ft) {
        if (lexer.token == Symbol.MULT || lexer.token == Symbol.DIV) {
            
            // ******************** Talvez esteja errado **************************************
            ft.setMulop(lexer.getStringValue().toCharArray()[0]);//*
            // ********************************************************************************
            
            lexer.nextToken();
            
            ft.setPostfixExpr(new PostfixExpr());
            
            if (postfix_expr(ft.getPostfixExpr())) {
                ft.setFactorTail(new FactorTail());
                return factor_tail(ft.getFactorTail());
            } else {
                return false;
            }
        }
        return true;
    }

    // postfix_expr -> primary | call_expr
    public boolean postfix_expr(PostfixExpr pe) {
    
        if (lexer.token == Symbol.IDENT) {
            Symbol temp = lexer.checkNextToken();
            if (temp == Symbol.LPAR) {
                return call_expr(pe);
            } else {
                return primary(pe);
            }
        } else {
            return primary(pe);
        }
    }

    // call_expr -> id ( {expr_list} )
    public boolean call_expr(PostfixExpr pe) {
        
        ExprList el = new ExprList();
        if (lexer.token != Symbol.IDENT) {
            error.signal("Missing identifier at call_expr()");
        }
        Ident id = new Ident(lexer.getStringValue());
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Expecting begin parentheses at call_expr()");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.RPAR) {
            el = expr_list();
        }
        if (lexer.token != Symbol.RPAR) {
            error.signal("Expecting close parentheses at call_expr()");
        }
        lexer.nextToken();
        
        CallExpr c = new CallExpr(id, el);
        pe.setCallExpr(c);
        
        return true;
    }
    
        // call_stmt -> call_expr
    public CallStmt call_stmt() {
        
        ExprList el = new ExprList();
        if (lexer.token != Symbol.IDENT) {
            error.signal("Missing identifier at call_expr()");
        }
        Ident id = new Ident(lexer.getStringValue());
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Expecting begin parentheses at call_expr()");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.RPAR) {
            el = expr_list();
        }
        if (lexer.token != Symbol.RPAR) {
            error.signal("Expecting close parentheses at call_expr()");
        }
        lexer.nextToken();
       
        return new CallStmt(new CallExpr(id, el));
    }

    // expr_list -> expr expr_list_tail
    public ExprList expr_list() {
        ExprList el = new ExprList();
        el.add(expr());
        expr_list_tail(el);
        return el;
    }

    // expr_list_tail -> , expr expr_list_tail | empty
    public void expr_list_tail(ExprList el) {
        if (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            el.add(expr());
            expr_list_tail(el);
        }
    }

    // primary -> (expr) | id | INTLITERAL | FLOATLITERAL
    public boolean primary(PostfixExpr pe) {
        Ident id = null;
        String literal = null;
        Primary p = null;
        if (lexer.token == Symbol.LPAR) {
            lexer.nextToken();
            p = new Primary(expr());
            if (lexer.token != Symbol.RPAR) {
                error.signal("Missing close parentheses at primary()");
            }
        } else if (lexer.token != Symbol.IDENT && lexer.token != Symbol.INTLITERAL && lexer.token != Symbol.FLOATLITERAL) {
            error.signal("Not a primary element at primary()");
        }
        if(lexer.token == Symbol.IDENT){
            id = new Ident(lexer.getStringValue());
            p = new Primary(id);
        } else {
            literal = lexer.getStringValue();
            p = new Primary(literal);
        }
        pe.setPrimary(p);
        lexer.nextToken();
        return true;
    }

    // addop -> + | -
    public void addop() {
        if (lexer.token != Symbol.PLUS && lexer.token != Symbol.MINUS) {
            error.signal("Wrong operator at addop()");
        }
        lexer.nextToken();
    }

    // mulop -> * | /
    public void mulop() {
        if (lexer.token != Symbol.MULT && lexer.token != Symbol.DIV) {
            error.signal("Wrong operator at mulop()");
        }
        lexer.nextToken();
    }

    /**
     * *********************************
     */
    /* Complex Statements and Condition */
    /**
     * *********************************
     */
    // if_stmt -> IF ( cond ) THEN stmt_list else_part ENDIF
    public IfStmt if_stmt() {
        Cond c = null;
        ArrayList<Stmt> ifpart = new ArrayList<Stmt>();
        ArrayList<Stmt> elsepart = null;
        if (lexer.token == Symbol.IF) {

            if (lexer.nextToken() != Symbol.LPAR) {
                error.signal("Missing parantheses at if_stmt()");
            }
            lexer.nextToken();

            c = cond();

            if (lexer.token != Symbol.RPAR) {
                error.signal("Missing parantheses at if_stmt()");
            }

            if (lexer.nextToken() != Symbol.THEN) {
                error.signal("Missing THEN keyword at if_stmt()");
            }

            lexer.nextToken();

            stmt_list(ifpart);

            else_part(elsepart);

            if (lexer.token != Symbol.ENDIF) {
                error.signal("Missing ENDIF keyword at if_stmt()");
            }

            lexer.nextToken();
        } else {
            error.signal("Missing IF keyword at if_stmt()");
        }
        return new IfStmt(c, new StmtList(ifpart), new StmtList(elsepart));
    }

    // else_part -> ELSE stmt_list | empty
    public void else_part(ArrayList<Stmt> elsepart) {
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            elsepart = new ArrayList<Stmt>();
            stmt_list(elsepart);
        }
    }

    // cond -> expr compop expr
    public Cond cond() {
        Cond c = new Cond();
        c.setExpr1(expr());

        c.setOp(compop());

        c.setExpr2(expr());
        return c;
    }

    // compop -> < | > | =
    public Character compop() {
        if (lexer.token != Symbol.LT && lexer.token != Symbol.GT && lexer.token != Symbol.EQUAL) {
            error.signal("Missing comparison operator at compop()");
        }
        Character c = lexer.getStringValue().toCharArray()[0];
        lexer.nextToken();
        return c;
    }

    // for_stmt -> FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
    public ForStmt for_stmt() {
        ForStmt forstmt = new ForStmt();
        if (lexer.token == Symbol.FOR) {

            if (lexer.nextToken() != Symbol.LPAR) {
                error.signal("Missing parantheses at for_stmt()");
            }

            lexer.nextToken();

            if (lexer.token != Symbol.SEMICOLON) {
                forstmt.setInit(assign_expr());
            }

            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Missing end of declaration at for_stmtf()");
            }

            if (lexer.nextToken() != Symbol.SEMICOLON) {
                forstmt.setCond(cond());
            }

            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Missing end of declaration at for_stmt()");
            }

            if (lexer.nextToken() != Symbol.RPAR) {
                forstmt.setLoop(assign_expr());
            }

            if (lexer.token != Symbol.RPAR) {
                error.signal("Missing parantheses at for_stmt()");
            }
            lexer.nextToken();

            ArrayList<Stmt> array = new ArrayList<Stmt>();
            stmt_list(array);
            forstmt.setSl(new StmtList(array));

            if (lexer.token != Symbol.ENDFOR) {
                error.signal("Missing ENDFOR keyword at for_stmt()");
            }
            lexer.nextToken();

        } else {
            error.signal("Missing FOR keyword at for_stmt()");
        }
        return forstmt;
    }

    private Lexer lexer;
    private CompilerError error;

}
