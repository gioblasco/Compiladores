
import Lexer.Symbol;
import Lexer.Lexer;
import Error.CompilerError;
import AST.*;
import Semantic.*;

import java.util.*;


public class Compiler {

    // para geracao de codigo
    public static final boolean GC = false;

    public Program compile(char[] p_input) {
        error = new CompilerError(null);
        lexer = new Lexer(p_input, error);
        error.setLexer(lexer);
        symbolTable = new SymbolTable();
        lexer.nextToken();
        Program p = program();
        if (lexer.token != Symbol.EOF) {
            error.show("Not expected ''" + lexer.token + "' after END keyword (end of file).'");
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
            error.show("Missing PROGRAM keyword at program()");
        }

        lexer.nextToken();

        if (lexer.token != Symbol.IDENT) {
            error.show("Missing PROGRAM Identifier at program()");
        }

        Ident id = new Ident(lexer.getStringValue());

        lexer.nextToken();

        if (lexer.token != Symbol.BEGIN) {
            error.show("Missing BEGIN keyword at program()");
        }

        lexer.nextToken();

        ProgramBody pgm = pgm_body();     
            
        if (lexer.token != Symbol.END) {
            error.show("Missing END keyword at program()");
        }

        String maiusculo = symbolTable.getFunction("MAIN");
        String minusculo = symbolTable.getFunction("main");
        if(maiusculo == null && maiusculo == null){
            error.show("The program must have a MAIN function!");
        } else if (maiusculo != null && !maiusculo.toLowerCase().equals("int"))
            error.show("The program must have a INT MAIN function!");
        else if (minusculo != null && !minusculo.toLowerCase().equals("int"))
            error.show("The program must have a INT MAIN function!");
        
        lexer.nextToken();

        return new Program(id, pgm);
    }

    // pgm_body -> decl func_declarations
    public ProgramBody pgm_body() {

        Declaration dec = decl(null, 1);
        FunctionDeclarations func = func_declarations();

        return new ProgramBody(dec, func);
    }

    // decl -> string_decl_list {decl} | var_decl_list {decl} | empty
    //lg means local or global -> if 1 ? global : local
    public Declaration decl(Declaration d, Integer lg) {
        if (lexer.token == Symbol.STRING) {
            if (d == null) {
                d = new Declaration();
            }
            // devemos concatenar as strings
            StringDeclList sd = string_decl_list(d.getSd(), lg);
            d.setStringDeclList(sd);

            decl(d, lg);
        } else if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            if (d == null) {
                d = new Declaration();
            }
            // devemos concatenar as variáveis
            VarDeclList vd = var_decl_list(d.getVd(), lg);
            d.setVarDeclList(vd);
            
            decl(d, lg);
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
    public StringDeclList string_decl_list(StringDeclList sd, Integer lg) {
        if (sd == null) {
            sd = new StringDeclList();
        }
        string_decl(sd, lg);
        if (lexer.token == Symbol.STRING) {
            string_decl_tail(sd, lg);
        }
        return sd;
    }

    // string_decl -> STRING id := str ; | empty
    public void string_decl(StringDeclList sd, Integer lg) {
        if (lexer.token == Symbol.STRING) {
            
            String type = lexer.getStringValue();

            if (lexer.nextToken() != Symbol.IDENT) {
                error.show("Missing identifier in string declaration");
            }
            
            switch(lg) {
                case 1: // contexto global
                    Type temp = symbolTable.getInGlobal(lexer.getStringValue());
                    if ( temp!=null && temp.isFunction() ) 
                        error.show("There is already a function with the name " + lexer.getStringValue());
                    else if(temp != null)
                        error.show("There is already a variable with the name " + lexer.getStringValue());
                    symbolTable.putInGlobal(lexer.getStringValue(), new Type(type, false));
                break;
                case 2: // contexto local
                    if ( symbolTable.getInLocal(lexer.getStringValue()) != null ) 
                        error.show("Variable " + lexer.getStringValue() + " has already been declared");
                    symbolTable.putInLocal(lexer.getStringValue(), type);
                break;
            }

            Ident id = new Ident(lexer.getStringValue());

            if (lexer.nextToken() != Symbol.ASSIGN) {
                error.show("Missing assignment symbol");
            }

            lexer.nextToken();

            if (lexer.token != Symbol.STRINGLITERAL) {
                error.show("Not a STRINGLITERAL");
            }

            String str = lexer.getStringValue();

            sd.addDecl(new StringDecl(id, str));

            lexer.nextToken();

            if (lexer.token != Symbol.SEMICOLON) {
                error.show("Missing end of declaration");
            }

            lexer.nextToken();
        }
    }

    // string_decl_tail -> string_decl {string_decl_tail}
    public void string_decl_tail(StringDeclList sd, Integer lg) {
        string_decl(sd, lg);
        if (lexer.token == Symbol.STRING) {
            string_decl_tail(sd, lg);
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
    public VarDeclList var_decl_list(VarDeclList vd, Integer lg) {
        if (vd == null) {
            vd = new VarDeclList();
        }
        var_decl(vd, lg);
        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            var_decl_tail(vd, lg);
        }
        return vd;
    }

    // var_decl -> var_type id_list ; | empty
    public void var_decl(VarDeclList vd, Integer lg) {
        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            String temp = lexer.getStringValue();
            lexer.nextToken();
            IdList list = id_list(lg);
            ArrayList<Ident> iter = list.getIdList();
            
            //entra aqui se nenhuma variavel declarada em id_list já existe
            for(Ident x: iter){
                switch(lg) {
                    case 1:
                        symbolTable.putInGlobal(x.getName(), new Type(temp, false));
                    break;
                    case 2:
                        symbolTable.putInLocal(x.getName(), temp);
                    break;
                }
            }
            vd.addDecl(new VarDecl(temp, list));
            if (lexer.token != Symbol.SEMICOLON) {
                error.show("Missing end of declaration at var_decl()");
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
            error.show("Wrong type");
        }
        lexer.nextToken();
    }

    // id_list -> id id_tail
    // if lg = 0 então get else if lg = 1 então getGlobal else if lg = 2 então getLocal
    public IdList id_list(Integer lg) {
        ArrayList<Ident> al = new ArrayList<Ident>();
        if (lexer.token == Symbol.IDENT) {
            switch(lg) {
                case 0:
                    if ( symbolTable.getVariable(lexer.getStringValue()) == null ) 
                        error.show("Variable " + lexer.getStringValue() + " hasn't been declared");
                break;
                case 1:
                    Type temp = symbolTable.getInGlobal(lexer.getStringValue());
                    if ( temp!=null && temp.isFunction() ) 
                        error.show("There is already a function with the name " + lexer.getStringValue());
                    else if(temp != null)
                        error.show("There is already a variable with the name " + lexer.getStringValue());
                break;
                case 2:
                    if ( symbolTable.getInLocal(lexer.getStringValue()) != null ) 
                        error.show("Variable " + lexer.getStringValue() + " has already been declared");
                break;
            }
            al.add(new Ident(lexer.getStringValue()));
            lexer.nextToken();
            id_tail(al, lg);
        } else {
            error.show("Wrong id_list declaration");
        }
        return new IdList(al);
    }

    // id_tail -> , id id_tail | empty
    public void id_tail(ArrayList<Ident> al, Integer lg) {
        if (lexer.token == Symbol.COMMA) {
            lexer.nextToken();

            if (lexer.token != Symbol.IDENT) {
                error.show("Missing identifier at id_tail()");
            }
            switch(lg) {
                case 0:
                    if ( symbolTable.getVariable(lexer.getStringValue()) == null ) 
                        error.show("Variable " + lexer.getStringValue() + " hasn't been declared");
                break;
                case 1:
                    Type temp = symbolTable.getInGlobal(lexer.getStringValue());
                    if ( temp!=null && temp.isFunction() ) 
                        error.show("There is already a function with the name " + lexer.getStringValue());
                    else if(temp != null)
                        error.show("There is already a variable with the name " + lexer.getStringValue());
                break;
                case 2:
                    if ( symbolTable.getInLocal(lexer.getStringValue()) != null ) 
                        error.show("Variable " + lexer.getStringValue() + " has already been declared");
                break;
            }
            al.add(new Ident(lexer.getStringValue()));
            lexer.nextToken();

            id_tail(al, lg);
        }
    }

    // var_decl_tail -> var_decl {var_decl_tail}
    public void var_decl_tail(VarDeclList vd, Integer lg) {
        var_decl(vd, lg);
        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            var_decl_tail(vd, lg);
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
            error.show("Missing correct variable type at param_decl()");
        }

        String type = lexer.getStringValue();
        
        lexer.nextToken();

        if (lexer.token != Symbol.IDENT) {
            error.show("Missing identifier at param_decl()");
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
                error.show("Missing function type");
            }
           
            String type = lexer.getStringValue();
            lexer.nextToken();

            if (lexer.token != Symbol.IDENT) {
                error.show("Missing identifier at func_decl()");
            }

            Ident id = new Ident(lexer.getStringValue());
            
            if ( symbolTable.getInGlobal(id.getName()) != null ) 
                error.show("Function " + lexer.getStringValue() + " has already been declared");
            
            lexer.nextToken();

            if (lexer.token != Symbol.LPAR) {
                error.show("Missing parantheses for parameters at func_decl()");
            }
            lexer.nextToken();

            if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
                if(id.getName().toLowerCase() == "main"){
                    error.show("Main function cannot receive parameters");
                }
                pdl = param_decl_list();
                //salva parametros na hashtablea local
                for(ParamDecl x : pdl.getParamList()){
                    symbolTable.putInLocal(x.getId().getName(), x.getType());
                }
                
                //salva assinatura da função no escopo global
                symbolTable.putInGlobal(id.getName(), new Type(type, true, pdl.getParamTypes()));
            } else
                symbolTable.putInGlobal(id.getName(), new Type(type, true, null));
          
            if (lexer.token != Symbol.RPAR) {
                error.show("Missing parantheses for parameters at func_decl()");
            }
            lexer.nextToken();

            if (lexer.token != Symbol.BEGIN) {
                error.show("Missing BEGIN keyword at func_decl()");
            }
            lexer.nextToken();

            FuncBody fb = func_body();
            
            ArrayList<Stmt> stmts = fb.getStmtList().getArrayList();
            boolean ret = false;
            for(Stmt x: stmts){
                if(x instanceof ReturnStmt){
                    // verifica aqui se tem return quando é void
                    if(type.equals("VOID"))
                        error.show("Void function with return statement");
                    // verifica aqui se o tipo do expr do return é igual ao tipo da função
                    else if(!type.equals(((ReturnStmt) x).getExpr().getType(symbolTable))){
                        error.show("Function "+id.getName()+" has a return with a different type");
                    }
                    ret = true;
                }
                else if(x instanceof AssignStmt){
                    String var = ((AssignStmt) x).getAssignExpr().getIdent().getName();
                    // checa uso de variaveis não usadas
                    if(symbolTable.getVariable(var) == null)
                        error.show("Trying to use variable "+var+" that hasn't been declared");
                }
            }
            // verifica aqui se tem return quando não é void
            if(!type.equals("VOID") && ret == false){
                error.show("Non void function "+id.getName()+" without return statement");
            }

            if (lexer.token != Symbol.END) {
                error.show("Missing END keyword at func_decl()");
            }
            lexer.nextToken();
            
            al.add(new FuncDecl(type, id, pdl, fb));
            
            //acabou esta função, então removemos todas as declarações locais
            symbolTable.removeLocalIdent();
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
        Declaration dec = decl(null, 2);
        ArrayList<Stmt> alstmt = null;
        alstmt = stmt_list(alstmt);

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
    public ArrayList<Stmt> stmt_list(ArrayList<Stmt> alstmt) {
        if (lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR || lexer.token == Symbol.IDENT) {
            alstmt = new ArrayList<Stmt>();
            stmt(alstmt);
            stmt_tail(alstmt);
        }
        return alstmt;
    }

    // stmt_tail -> stmt stmt_tail | empty
    public void stmt_tail(ArrayList<Stmt> alstmt) {
        if (lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR || lexer.token == Symbol.IDENT) {
            stmt(alstmt);
            stmt_tail(alstmt);
        }
    }

    // stmt -> assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt | call_stmt
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
                    error.show("Missing semicolon after call_expr() at stmt()");
                }
                lexer.nextToken();
            } else {
                error.show("Wrong use of element after identifier at stmt()");
            }
        } else {
            error.show("Wrong statement declaration at stmt()");
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
            error.show("Semicolon expected at assign_stmt()" + lexer.getStringValue());
        }
        lexer.nextToken();

        return new AssignStmt(ae);
    }

    // assign_expr -> id := expr
    public AssignExpr assign_expr() {
        if (lexer.token != Symbol.IDENT) {
            error.show("Expecting identifier at assign_expr()");
        }
        
        String type = symbolTable.getVariable(lexer.getStringValue());
        if(type == null){
            error.show("Tried to assign a value to a variable ("+lexer.getStringValue()+") that hasn't been declared.");
        } else {
            if(type == "STRING"){
                error.show("Tried to assign a value to a declared string variable ("+lexer.getStringValue()+")");
            }
        }
        
        Ident id = new Ident(lexer.getStringValue());
        
        lexer.nextToken();
        if (lexer.token != Symbol.ASSIGN) {
            error.show("Expecting assign show assign_expr()");
        }
        lexer.nextToken();
        Expr e = expr();

        if(type.toLowerCase().equals("int") && e.getType(symbolTable).toLowerCase().equals("float")){
            error.show("Trying to assign a "+e.getType(symbolTable)+" to a "+type);
        } else if (!type.equals(e.getType(symbolTable)))
            error.warning("Trying to assign a "+e.getType(symbolTable)+" to a "+type);
        
        return new AssignExpr(id, e);
    }

    // read_stmt -> READ ( id_list );
    public ReadStmt read_stmt() {
        if (lexer.token != Symbol.READ) {
            error.show("Missing READ keyword at read_stmt()");
        }
        if (lexer.nextToken() != Symbol.LPAR) {
            error.show("Missing open parentheses at read_stmt()");
        }
        lexer.nextToken();
        IdList il = id_list(0);
        if (lexer.token != Symbol.RPAR) {
            error.show("Missing close parentheses at read_stmt()");
        }
        if (lexer.nextToken() != Symbol.SEMICOLON) {
            error.show("Semicolon expected at read_stmt()");
        }
        lexer.nextToken();
        return new ReadStmt(il, symbolTable, this.error );
    }

    // write_stmt -> WRITE ( id_list );
    public WriteStmt write_stmt() {
        if (lexer.token != Symbol.WRITE) {
            error.show("Missing READ keyword at write_stmt()");
        }
        if (lexer.nextToken() != Symbol.LPAR) {
            error.show("Missing open parentheses at write_stmt()");
        }
        lexer.nextToken();
        IdList il = id_list(0);
        if (lexer.token != Symbol.RPAR) {
            error.show("Missing close parentheses write_stmt()");
        }
        if (lexer.nextToken() != Symbol.SEMICOLON) {
            error.show("Semicolon expected at write_stmt()");
        }
        lexer.nextToken();
        return new WriteStmt(il, this.symbolTable, this.error);
    }

    // return_stmt -> RETURN expr ;
    public ReturnStmt return_stmt() {
        if (lexer.token != Symbol.RETURN) {
            error.show("Missing RETURN keyword at return_stmt()");
        }
        lexer.nextToken();
        Expr e = expr();
        if (lexer.token != Symbol.SEMICOLON) {
            error.show("Semicolon expected at return_stmt()");
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
     * @return 
     */
    // expr -> factor expr_tail
    public Expr expr() {
        Expr e = new Expr();
        if (factor(e)) {
            e.setExprTail(expr_tail());
            if(e.getExprTail() != null){
                String typeofft = e.getFactor().getType(symbolTable);
                String typeofet = e.getExprTail().getType(symbolTable);
                if(!typeofft.equals(typeofet))
                    error.warning("Trying to make an operation between a "+typeofft+" and a "+typeofet);
            }
        } else {
            error.show("Factor expected at expr()");
        }
        return e;
    }

    // expr_tail -> addop factor expr_tail | empty
    public ExprTail expr_tail() {
        ExprTail et = null;
        if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS) {
            et = new ExprTail();
            et.setAddop(lexer.getStringValue().toCharArray()[0]);
            lexer.nextToken();
            if (factor(et)) {
                et.setExprTail(expr_tail());
            } else {
                error.show("Wrong ExprTail");
            }
        }
        return et;
    }
    
     

    // factor -> postfix_expr factor_tail
    public boolean factor(Expr e) {
        e.setFactor(new Factor());
        PostfixExpr pe = new PostfixExpr();
        if (postfix_expr(pe)) {
            e.getFactor().setPostfixExpr(pe);
            boolean fct = factor_tail(e.getFactor());
            if(fct && e.getFactor().getFactorTail() != null){
                String typeofpf = e.getFactor().getPostfixExpr().getType(symbolTable);
                String typeofft = e.getFactor().getFactorTail().getType(symbolTable);
                if(!typeofpf.equals(typeofft))
                    error.warning("Trying to make an operation between a "+typeofpf+" and a "+typeofft);
            }
            return fct;      
        }
        
        return false;
    }
    


    // factor_tail -> mulop postfix_expr factor_tail | empty
    public boolean factor_tail(Factor f) {
        if (lexer.token == Symbol.MULT || lexer.token == Symbol.DIV) {
            f.setFactorTail(new FactorTail());
           
            f.getFactorTail().setMulop(lexer.getStringValue().toCharArray()[0]);
           
            lexer.nextToken();
            
            f.getFactorTail().setPostfixExpr(new PostfixExpr());
            
            if (postfix_expr(f.getFactorTail().getPostfixExpr())) {
                f.getFactorTail().setFactorTail(new FactorTail());
                return factor_tail(f.getFactorTail());
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
        ExprList el = null;
        if (lexer.token != Symbol.IDENT) {
            error.show("Missing identifier at call_expr()");
        }
        Ident id = new Ident(lexer.getStringValue());
        
        if(symbolTable.getFunction(id.getName()) == null){
            error.show("Function "+id.getName()+" hasn't been declared.");
        }
         
        if (lexer.nextToken() != Symbol.LPAR) {
            error.show("Expecting begin parentheses at call_expr()");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.RPAR) {
            el = expr_list(el);
        }
        ArrayList<String> signature = symbolTable.getInGlobal(id.getName()).getSignature();
        if(el != null && signature != null){
            ArrayList<Expr> parameters = el.getExprList();
            if(parameters.size() == signature.size()){
                for (int i = 0; i < parameters.size(); i++) {
                    if(!parameters.get(i).getType(symbolTable).toLowerCase().equals(signature.get(i).toLowerCase())){
                        error.show("Calling expression "+id.getName()+" with wrong type of parameters");
                    }
                }
            } else
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        } else if(el == null && signature != null)
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        else if(el != null && signature == null)
            error.show("Calling expression "+id.getName()+" with wrong number of parameters");
            
            
        if (lexer.token != Symbol.RPAR) {
            error.show("Expecting close parentheses at call_expr()");
        }
        lexer.nextToken();
        
        CallExpr c = new CallExpr(id, el);
        pe.setCallExpr(c);
        
        return true;
    }
    
        // call_stmt -> call_expr ;
    public CallStmt call_stmt() {
        
        ExprList el = null;
        if (lexer.token != Symbol.IDENT) {
            error.show("Missing identifier at call statement");
        }
        Ident id = new Ident(lexer.getStringValue());
        
        if(symbolTable.getFunction(id.getName()) == null){
            error.show("Function "+id.getName()+" hasn't been declared.");
        }
        
        if (lexer.nextToken() != Symbol.LPAR) {
            error.show("Expecting begin parentheses at call statement");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.RPAR) {
            el = expr_list(el);
        }
        ArrayList<String> signature = symbolTable.getInGlobal(id.getName()).getSignature();
        if(el != null && signature != null){
            ArrayList<Expr> parameters = el.getExprList();
            if(parameters.size() == signature.size()){
                for (int i = 0; i < parameters.size(); i++) {
                    if(!parameters.get(i).getType(symbolTable).toLowerCase().equals(signature.get(i).toLowerCase())){
                        error.show("Calling expression "+id.getName()+" with wrong type of parameters");
                    }
                }
            } else
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        } else if(el == null && signature != null)
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        else if(el != null && signature == null)
            error.show("Calling expression with "+id.getName()+" wrong number of parameters");
        
        if (lexer.token != Symbol.RPAR) {
            error.show("Expecting close parentheses at call statement");
        }
        lexer.nextToken();
       
        return new CallStmt(new CallExpr(id, el));
    }

    // expr_list -> expr expr_list_tail
    public ExprList expr_list(ExprList el) {
        if (el == null) {
            el = new ExprList();
        }
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
                error.show("Missing close parentheses at primary()");
            }
        } else if(lexer.token == Symbol.IDENT){
            id = new Ident(lexer.getStringValue());
            p = new Primary(id);
        } else if(lexer.token == Symbol.INTLITERAL){
            literal = lexer.getStringValue();
            p = new Primary(literal, "INT");
        } else if(lexer.token == Symbol.FLOATLITERAL){
             literal = lexer.getStringValue();
             p = new Primary(literal, "FLOAT");
        }
        else {
            error.show("Not a primary element at primary()");
        }
        pe.setPrimary(p);
        lexer.nextToken();
        return true;
    }

    // addop -> + | -
    public void addop() {
        if (lexer.token != Symbol.PLUS && lexer.token != Symbol.MINUS) {
            error.show("Wrong operator at addop()");
        }
        lexer.nextToken();
    }

    // mulop -> * | /
    public void mulop() {
        if (lexer.token != Symbol.MULT && lexer.token != Symbol.DIV) {
            error.show("Wrong operator at mulop()");
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
        ArrayList<Stmt> ifpart = null;
        ArrayList<Stmt> elsepart = null;
        if (lexer.token == Symbol.IF) {

            if (lexer.nextToken() != Symbol.LPAR) {
                error.show("Missing parantheses at if_stmt()");
            }
            lexer.nextToken();

            c = cond();

            if (lexer.token != Symbol.RPAR) {
                error.show("Missing parantheses at if_stmt()");
            }

            if (lexer.nextToken() != Symbol.THEN) {
                error.show("Missing THEN keyword at if_stmt()");
            }

            lexer.nextToken();

            ifpart = stmt_list(ifpart);

            elsepart = else_part(elsepart);

            if (lexer.token != Symbol.ENDIF) {
                error.show("Missing ENDIF keyword at if_stmt()");
            }

            lexer.nextToken();
        } else {
            error.show("Missing IF keyword at if_stmt()");
        }
        return new IfStmt(c, (ifpart != null) ? new StmtList(ifpart) : null, (elsepart != null) ? new StmtList(elsepart) : null);
    }

    // else_part -> ELSE stmt_list | empty
    public ArrayList<Stmt> else_part(ArrayList<Stmt> elsepart) {
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            elsepart = stmt_list(elsepart);
        }
        return elsepart;
    }

    // cond -> expr compop expr
    public Cond cond() {
        Cond c = new Cond();
        c.setExpr1(expr());
        c.setOp(compop());
        c.setExpr2(expr());
        
        String type1 = c.getExpr1().getType(symbolTable);
        String type2 = c.getExpr2().getType(symbolTable);
        
        if(!type1.equals(type2)){
            error.warning("Trying to compare a "+type1+" with a "+type2);
        }
       
        return c;
    }

    // compop -> < | > | =
    public Character compop() {
        if (lexer.token != Symbol.LT && lexer.token != Symbol.GT && lexer.token != Symbol.EQUAL) {
            error.show("Missing comparison operator at compop()");
        }
        Character c = lexer.getStringValue().toCharArray()[0];
        lexer.nextToken();
        return c;
    }

    // for_stmt -> FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
    public ForStmt for_stmt() {
        ForStmt forstmt = new ForStmt();
        ArrayList<Stmt> array = null;
        if (lexer.token == Symbol.FOR) {

            if (lexer.nextToken() != Symbol.LPAR) {
                error.show("Missing parantheses at for_stmt()");
            }

            lexer.nextToken();

            if (lexer.token != Symbol.SEMICOLON) {
                forstmt.setInit(assign_expr());
            }

            if (lexer.token != Symbol.SEMICOLON) {
                error.show("Missing end of declaration at for_stmtf()");
            }

            if (lexer.nextToken() != Symbol.SEMICOLON) {
                forstmt.setCond(cond());
            }

            if (lexer.token != Symbol.SEMICOLON) {
                error.show("Missing end of declaration at for_stmt()");
            }

            if (lexer.nextToken() != Symbol.RPAR) {
                forstmt.setLoop(assign_expr());
            }

            if (lexer.token != Symbol.RPAR) {
                error.show("Missing parantheses at for_stmt()");
            }
            lexer.nextToken();

            array = stmt_list(array);
            forstmt.setSl(new StmtList(array));

            if (lexer.token != Symbol.ENDFOR) {
                error.show("Missing ENDFOR keyword at for_stmt()");
            }
            lexer.nextToken();

        } else {
            error.show("Missing FOR keyword at for_stmt()");
        }
        return forstmt;
    }

    private SymbolTable symbolTable;
    private Lexer lexer;
    private CompilerError error;

}
