
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
            error.signal("Missing PROGRAM keyword");
        }

        lexer.nextToken();

        if (lexer.token != Symbol.IDENT) {
            error.signal("Missing PROGRAM identifier");
        }

        Ident id = new Ident(lexer.getStringValue());

        lexer.nextToken();

        if (lexer.token != Symbol.BEGIN) {
            error.signal("Missing BEGIN keyword to PROGRAM");
        }

        lexer.nextToken();

        ProgramBody pgm = pgm_body();     
            
        if (lexer.token != Symbol.END) {
            error.signal("Missing END keyword to PROGRAM");
        }

        if(symbolTable.getFunction("main") == null)
            error.show("The program must have a main function");
            
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
                error.signal("Missing identifier in string declaration");
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
                error.signal("Missing assignment symbol at string declaration");
            }

            lexer.nextToken();

            if (lexer.token != Symbol.STRINGLITERAL) {
                error.signal("Not assigning a STRINGLITERAL to "+id.getName());
            }

            String str = lexer.getStringValue();

            sd.addDecl(new StringDecl(id, str));

            lexer.nextToken();

            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Missing end of declaration after string declaration");
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
                error.signal("Missing end of declaration at variable declaration");
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
            error.signal("Wrong declaration of a list of identifiers");
        }
        return new IdList(al);
    }

    // id_tail -> , id id_tail | empty
    public void id_tail(ArrayList<Ident> al, Integer lg) {
        if (lexer.token == Symbol.COMMA) {
            lexer.nextToken();

            if (lexer.token != Symbol.IDENT) {
                error.signal("Not an identifier after comma");
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
            error.signal("Wrong or missing variable type at parameters declaration");
        }

        String type = lexer.getStringValue();
        
        lexer.nextToken();

        if (lexer.token != Symbol.IDENT) {
            error.signal("Wrong or missing identifier at parameters declaration");
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
                error.signal("Wrong or missing function type at function declaration");
            }
           
            String type = lexer.getStringValue();
            lexer.nextToken();

            if (lexer.token != Symbol.IDENT) {
                error.signal("Missing identifier at function declaration");
            }

            Ident id = new Ident(lexer.getStringValue());
            
            if(id.getName().equals("main") && !type.equals("INT")){
                    error.show("The main function must be INT");
            }
            
            if ( symbolTable.getInGlobal(id.getName()) != null ) 
                error.show("Function " + lexer.getStringValue() + " has already been declared");
            
            lexer.nextToken();

            if (lexer.token != Symbol.LPAR) {
                error.signal("Missing open parantheses for parameters at function declaration");
            }
            lexer.nextToken();

            if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
                if(id.getName().equals("main")){
                    error.show("The main function cannot receive parameters");
                } 
                pdl = param_decl_list();
                
                //salva parametros na hashtablea local
                for(ParamDecl x : pdl.getParamList()){
                    symbolTable.putInLocal(x.getId().getName(), x.getType());
                }
                
                //salva assinatura da função no escopo global
                symbolTable.putInGlobal(id.getName(), new Type(type, true, pdl.getParamTypes()));
            } else {
                symbolTable.putInGlobal(id.getName(), new Type(type, true, null));              
            }
          
            if (lexer.token != Symbol.RPAR) {
                error.signal("Missing close parantheses for parameters at function declaration");
            }
            lexer.nextToken();

            if (lexer.token != Symbol.BEGIN) {
                error.signal("Missing BEGIN keyword at function declaration");
            }
            lexer.nextToken();

            FuncBody fb = func_body();
            
            ArrayList<Stmt> stmts = fb.getStmtList().getArrayList();
            boolean ret = false;
            for(Stmt x: stmts){
                if(x instanceof ReturnStmt){
                    String typeofreturn = ((ReturnStmt) x).getExpr().getType(symbolTable);
                    // verifica aqui se tem return quando é void
                    if(type.equals("VOID"))
                        error.show("Void function with return statement");
                    // verifica aqui se o tipo do expr do return é igual ao tipo da função
                    else if(typeofreturn != null && !type.equals(typeofreturn)){
                        error.show("Function "+id.getName()+" has a return with a different type");
                    }
                    ret = true;
                }
            }
            // verifica aqui se tem return quando não é void
            if(!type.equals("VOID") && ret == false){
                error.show("Non void function "+id.getName()+" without return statement");
            }

            if (lexer.token != Symbol.END) {
                error.signal("Missing END keyword at function declaration");
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
                    error.signal("Missing semicolon after call statement");
                }
                lexer.nextToken();
            } else {
                error.signal("Wrong use of element after identifier");
            }
        } else {
            error.signal("Wrong use of statement declaration");
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
            error.signal("Semicolon expected after assignment");
        }
        lexer.nextToken();

        return new AssignStmt(ae);
    }

    // assign_expr -> id := expr
    public AssignExpr assign_expr() {
        if (lexer.token != Symbol.IDENT) {
            error.signal("Expecting identifier at assign expression");
        }
        
        String type = symbolTable.getVariable(lexer.getStringValue());
        if(type == null){
            error.show("Tried to assign a value to a variable ("+lexer.getStringValue()+") that hasn't been declared.");
        } else {
            if(type.equals("STRING")){
                error.show("Tried to assign a value to a declared string variable ("+lexer.getStringValue()+")");
            }
        }
        
        Ident id = new Ident(lexer.getStringValue());
        
        lexer.nextToken();
        if (lexer.token != Symbol.ASSIGN) {
            error.signal("Expecting assign symbol at assign expression");
        }
        lexer.nextToken();
        Expr e = expr();

        if(type != null && type.toLowerCase().equals("int") && e.getType(symbolTable) != null && e.getType(symbolTable).toLowerCase().equals("float"))
            error.show("Trying to assign a "+e.getType(symbolTable)+" to a "+type);
        else if (type != null && e.getType(symbolTable) != null && !type.equals(e.getType(symbolTable)))
            error.warning("Trying to assign a "+e.getType(symbolTable)+" to a "+type);
        
        return new AssignExpr(id, e);
    }

    // read_stmt -> READ ( id_list );
    public ReadStmt read_stmt() {
        if (lexer.token != Symbol.READ) {
            error.signal("Missing READ keyword at read statement");
        }
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Missing open parentheses for parameters at read statement");
        }
        lexer.nextToken();
        IdList il = id_list(0);
        if (lexer.token != Symbol.RPAR) {
            error.signal("Missing close parentheses for parameters at read statement");
        }
        if (lexer.nextToken() != Symbol.SEMICOLON) {
            error.signal("Semicolon expected after read statement");
        }
        lexer.nextToken();
        return new ReadStmt(il, symbolTable, this.error );
    }

    // write_stmt -> WRITE ( id_list );
    public WriteStmt write_stmt() {
        if (lexer.token != Symbol.WRITE) {
            error.signal("Missing WRITE keyword at write statement");
        }
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Missing open parentheses for parameters at write statement");
        }
        lexer.nextToken();
        IdList il = id_list(0);
        if (lexer.token != Symbol.RPAR) {
            error.signal("Missing close parentheses for parameters at write statement");
        }
        if (lexer.nextToken() != Symbol.SEMICOLON) {
            error.signal("Semicolon expected after write statement");
        }
        lexer.nextToken();
        return new WriteStmt(il, this.symbolTable, this.error);
    }

    // return_stmt -> RETURN expr ;
    public ReturnStmt return_stmt() {
        if (lexer.token != Symbol.RETURN) {
            error.signal("Missing RETURN keyword at return statement");
        }
        lexer.nextToken();
        Expr e = expr();
        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Semicolon expected after return statement");
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
                if(typeofft != null && typeofet != null && !typeofft.equals(typeofet))
                    error.warning("Trying to make an operation between a "+typeofft+" and a "+typeofet);
            }
        } else {
            error.signal("Factor expected in expression");
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
                error.signal("Factor expected after operation symbol");
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
                if(typeofpf != null && typeofft != null && !typeofpf.equals(typeofft))
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
            error.signal("Missing identifier at call expression");
        }
        Ident id = new Ident(lexer.getStringValue());
        
        if(symbolTable.getFunction(id.getName()) == null){
            error.show("Function "+id.getName()+" hasn't been declared.");
        }
         
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Expecting begin parentheses for parameters at call expression");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.RPAR) {
            el = expr_list(el);
        }
        if(el != null && symbolTable.getInGlobal(id.getName()) != null && symbolTable.getInGlobal(id.getName()).getSignature() != null){
            ArrayList<String> signature = symbolTable.getInGlobal(id.getName()).getSignature();
            ArrayList<Expr> parameters = el.getExprList();
            if(parameters.size() == signature.size()){
                for (int i = 0; i < parameters.size(); i++) {
                    if(parameters.get(i).getType(symbolTable) != null && !parameters.get(i).getType(symbolTable).toLowerCase().equals(signature.get(i).toLowerCase())){
                        error.show("Calling expression "+id.getName()+" with wrong type of parameters");
                    }
                }
            } else
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        } else if(el == null && symbolTable.getInGlobal(id.getName()) != null && symbolTable.getInGlobal(id.getName()).getSignature() != null)
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        else if(el != null  && symbolTable.getInGlobal(id.getName()) != null && symbolTable.getInGlobal(id.getName()).getSignature() == null)
            error.show("Calling expression "+id.getName()+" with wrong number of parameters");
            
            
        if (lexer.token != Symbol.RPAR) {
            error.signal("Expecting close parentheses for parameters at call expression");
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
            error.signal("Missing identifier at call expression");
        }
        Ident id = new Ident(lexer.getStringValue());
        
        if(symbolTable.getFunction(id.getName()) == null){
            error.show("Function "+id.getName()+" hasn't been declared.");
        }
        
        if (lexer.nextToken() != Symbol.LPAR) {
            error.signal("Expecting begin parentheses for paramters at call expression");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.RPAR) {
            el = expr_list(el);
        }
        if(el != null && symbolTable.getInGlobal(id.getName()) != null && symbolTable.getInGlobal(id.getName()).getSignature() != null){
            ArrayList<String> signature = symbolTable.getInGlobal(id.getName()).getSignature();
            ArrayList<Expr> parameters = el.getExprList();
            if(parameters.size() == signature.size()){
                for (int i = 0; i < parameters.size(); i++) {
                    if(parameters.get(i).getType(symbolTable) != null && !parameters.get(i).getType(symbolTable).toLowerCase().equals(signature.get(i).toLowerCase())){
                        error.show("Calling expression "+id.getName()+" with wrong type of parameters");
                    }
                }
            } else
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        } else if(el == null && symbolTable.getInGlobal(id.getName()) != null && symbolTable.getInGlobal(id.getName()).getSignature() != null)
                error.show("Calling expression "+id.getName()+" with wrong number of parameters");
        else if(el != null && symbolTable.getInGlobal(id.getName()) != null && symbolTable.getInGlobal(id.getName()).getSignature() == null)
            error.show("Calling expression with "+id.getName()+" wrong number of parameters");
        
        if (lexer.token != Symbol.RPAR) {
            error.signal("Expecting close parentheses for parameters at call expression");
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
                error.signal("Missing close parentheses at expression");
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
            error.signal("Not a primary element");
        }
        pe.setPrimary(p);
        lexer.nextToken();
        return true;
    }

    // addop -> + | -
    public void addop() {
        if (lexer.token != Symbol.PLUS && lexer.token != Symbol.MINUS) {
            error.signal("Wrong operator for addop. Using not a plus or not a minus.");
        }
        lexer.nextToken();
    }

    // mulop -> * | /
    public void mulop() {
        if (lexer.token != Symbol.MULT && lexer.token != Symbol.DIV) {
            error.signal("Wrong operator for mulop. Not multiplication or division.");
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
                error.signal("Missing open parantheses for condition at if statement");
            }
            lexer.nextToken();

            c = cond();

            if (lexer.token != Symbol.RPAR) {
                error.signal("Missing close parantheses for condition at if statement");
            }

            if (lexer.nextToken() != Symbol.THEN) {
                error.signal("Missing THEN keyword at if statement");
            }

            lexer.nextToken();

            ifpart = stmt_list(ifpart);

            elsepart = else_part(elsepart);

            if (lexer.token != Symbol.ENDIF) {
                error.signal("Missing ENDIF keyword at if statement");
            }

            lexer.nextToken();
        } else {
            error.signal("Missing IF keyword at if statement");
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
        
        if(type1 != null && type2 != null){
            if(!type1.equals(type2)){
                error.warning("Trying to compare a "+type1+" with a "+type2);
            }
        } else
            error.show("Trying to make comparison with a variable(s) that hasn't been declared");
       
        return c;
    }

    // compop -> < | > | =
    public Character compop() {
        if (lexer.token != Symbol.LT && lexer.token != Symbol.GT && lexer.token != Symbol.EQUAL) {
            error.signal("Missing comparison operator for condition");
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
                error.signal("Missing open parantheses for conditions to loop");
            }

            lexer.nextToken();

            if (lexer.token != Symbol.SEMICOLON) {
                forstmt.setInit(assign_expr());
            }

            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Missing semicolon after initialization at for statement");
            }

            if (lexer.nextToken() != Symbol.SEMICOLON) {
                forstmt.setCond(cond());
            }

            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Missing semicolon after stop condition at for statement");
            }

            if (lexer.nextToken() != Symbol.RPAR) {
                forstmt.setLoop(assign_expr());
            }

            if (lexer.token != Symbol.RPAR) {
                error.signal("Missing close parantheses for conditions to loop");
            }
            lexer.nextToken();

            array = stmt_list(array);
            forstmt.setSl(new StmtList(array));

            if (lexer.token != Symbol.ENDFOR) {
                error.signal("Missing ENDFOR keyword at for statement");
            }
            lexer.nextToken();

        } else {
            error.signal("Missing FOR keyword at for statement");
        }
        return forstmt;
    }

    private SymbolTable symbolTable;
    private Lexer lexer;
    private CompilerError error;

}
