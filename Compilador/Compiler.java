import Lexer.*;
import Error.*;

public class Compiler {

	// para geracao de codigo
	public static final boolean GC = false;

    public void compile( char []p_input ) {

        error = new CompilerError(null);
        lexer = new Lexer(p_input, error);
				error.setLexer(lexer);
        lexer.nextToken();
        program();
        if(lexer.token!= Symbol.EOF)
            error.signal("Not expected ''" + lexer.token + "' after END keyword.'" );
    }

	/***************************************/
	/************** Program ****************/
	/***************************************/

    //   program ::= PROGRAM id BEGIN pgm_body END
    public void program(){

        if(lexer.token != Symbol.PROGRAM)
            error.signal("Error: Missing PROGRAM keyword at line: " + lexer.getLineNumber());

        lexer.nextToken();

        if(lexer.token!= Symbol.IDENT)
			error.signal("Error: Missing PROGRAM Identifier at line: " + lexer.getLineNumber());

		lexer.nextToken();

		if(lexer.token != Symbol.BEGIN)
            error.signal("Error: Missing BEGIN keyword at line: " + lexer.getLineNumber());

		lexer.nextToken();

		pgm_body();

        if(lexer.token!= Symbol.END)
            error.signal("Error: Missing END keyword at line:" + lexer.getLineNumber());

        lexer.nextToken();
    }

	// pgm_body -> decl func_declarations
	public void pgm_body(){

		decl();
		func_declarations();

	}



	// decl -> string_decl_list {decl} | var_decl_list {decl} | empty
	public void decl(){
		if(lexer.token == Symbol.STRING){
			string_decl_list();

			decl();
		}
		else if(lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_list();

			decl();
		}
	}

	/*****************************/
	/* Global String Declaration */
	/*****************************/

	// string_decl_list -> string_decl {string_decl_tail}
	public void string_decl_list(){
		string_decl();
		if(lexer.token == Symbol.STRING)
			string_decl_tail();
	}

	// string_decl -> STRING id := str ; | empty
	public void string_decl(){
			if(lexer.token == Symbol.STRING){
				lexer.nextToken();

				if(lexer.token != Symbol.IDENT)
					error.signal("Missing identifier in string declaration");
				lexer.nextToken();

				if(lexer.token != Symbol.ASSIGN)
					error.signal("Missing assignment symbol");
				lexer.nextToken();

				if(lexer.token != Symbol.STRING)
					error.signal("Missing STRINGLITERAL type");
				lexer.nextToken();

				if(lexer.token != Symbol.SEMICOLON)
					error.signal("Error: Missing end of declaration");
				lexer.nextToken();

			}
	}

	// string_decl_tail -> string_decl {string_decl_tail}
	public void string_decl_tail(){
		string_decl();
		if(lexer.token == Symbol.STRING)
			string_decl_tail();
	}

	/************************/
	/* Variable Declaration */
	/************************/

	// var_decl_list -> var_decl {var_decl_tail}
	public void var_decl_list(){
		var_decl();
		if(lexer.token != Symbol.FLOAT && lexer.token == Symbol.INT)
			var_decl_tail();
	}

	// var_decl -> var_type id_list ; | empty
	public void var_decl(){
		if(var_type()){
			id_list();
			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Missing end of declarationMissing end of declaration");
			lexer.nextToken();
		}
	}

	// var_type -> FLOAT | INT
	public boolean var_type(){
		if(lexer.token != Symbol.FLOAT && lexer.token == Symbol.INT)
			return false;
		lexer.nextToken();
		return true;
	}

	// any_type -> var_type | VOID
	public void any_type(){
		if(var_type())
			return;
		if(lexer.token != Symbol.VOID)
			error.signal("Wrong type");
		lexer.nextToken();
	}

	// id_list -> id id_tail
	public void id_list(){
		if(lexer.token == Symbol.IDENT){
			lexer.nextToken();
			id_tail();
		} else
				error.signal("Wrong id_list declaration");
	}

	// id_tail -> , id id_tail | empty
	public void id_tail(){
		if(lexer.token == Symbol.COMMA){
			lexer.nextToken();

			if(lexer.token != Symbol.IDENT)
				error.signal("Missing identifier!");
			lexer.nextToken();

			id_tail();
		}
	}

	// var_decl_tail -> var_decl {var_decl_tail}
	public void var_decl_tail(){
		var_decl();
		if(lexer.token != Symbol.FLOAT && lexer.token == Symbol.INT)
			var_decl_tail();
	}

	/*********************************/
	/**** Function Paramater List ****/
	/*********************************/

	// param_decl_list -> param_decl param_decl_tail
	public void param_decl_list(){
		param_decl();
		param_decl_tail();
	}

	// param_decl -> var_type id
	public void param_decl(){
		System.out.println("@@@" + lexer.token);

		if(!var_type())
			error.signal("Missing Correct Variable Type");
		System.out.println("@@@" +lexer.token);
		if(lexer.token != Symbol.IDENT)
			error.signal("Missing identifier!!");
		lexer.nextToken();
	}


	// param_decl_tail -> , param_decl param_decl_tail | empty
	public void param_decl_tail(){
		if(lexer.token == Symbol.COMMA){
			lexer.nextToken();

			param_decl();
			param_decl_tail();
		}
	}

	/***************************************/
	/******** Function Declarations ********/
	/***************************************/

	// func_declarations -> func_decl {func_decl_tail}
	public void func_declarations(){
		func_decl();
		if(lexer.token == Symbol.FUNCTION)
			func_decl_tail();
	}

	// func_decl -> FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
	public void func_decl(){
		if(lexer.token == Symbol.FUNCTION){
			lexer.nextToken();

			any_type();

			if(lexer.token != Symbol.IDENT)
				error.signal("Missing identifier in function declaration");
			lexer.nextToken();

			if(lexer.token != Symbol.LPAR)
				error.signal("Missing parantheses");
			lexer.nextToken();

			if(var_type())
				param_decl_list();

			if(lexer.token != Symbol.RPAR)
				error.signal("Missing parantheses");
			lexer.nextToken();

			if(lexer.token != Symbol.BEGIN)
				error.signal("Missing BEGIN keyword");
			lexer.nextToken();
			lexer.printToken();
			func_body();

			if(lexer.token != Symbol.END)
				error.signal("Missing END keyword");
			lexer.nextToken();
		}
	}

	// func_decl_tail -> func_decl {func_decl_tail}
	public void func_decl_tail(){
		func_decl();
		if(lexer.token == Symbol.FUNCTION)
			func_decl_tail();
	}

	// func_body -> decl stmt_list
	public void func_body(){
		decl();

		stmt_list();
	}

	/******************/
	/* Statement List */
	/******************/

	// stmt_list -> stmt stmt_tail | empty
	public void stmt_list(){
		if(lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR || lexer.token == Symbol.IDENT){
			stmt();
			stmt_tail();
		}
	}

	// stmt_tail -> stmt stmt_tail | empty
	public void stmt_tail(){
		if(lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR || lexer.token == Symbol.IDENT){
			stmt();
			stmt_tail();
		}
	}

	// stmt -> assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt | call_expr
	public void stmt(){
			if(lexer.token == Symbol.READ)
				read_stmt();
			else if(lexer.token == Symbol.WRITE)
				write_stmt();
			else if(lexer.token == Symbol.RETURN)
				return_stmt();
			else if(lexer.token == Symbol.IF)
				if_stmt();
			else if(lexer.token == Symbol.FOR)
				for_stmt();

			else if(lexer.token == Symbol.IDENT){
				Symbol temp = lexer.checkNextToken();
				if(temp == Symbol.ASSIGN)
					assign_stmt();
				else if(temp == Symbol.LPAR)
					call_expr();
				else
					error.signal("Wrong use of element after identifier");
			} else
					error.signal("Wrong statement declaration");
	}

	/********************/
	/* Basic Statements */
	/********************/

	// assign_stmt -> assign_expr ;
	public void assign_stmt(){
		assign_expr();
		if(lexer.token != Symbol.SEMICOLON)
			error.signal("Semicolon expected");
	}

	// assign_expr -> id := expr
	public void assign_expr(){
		if(lexer.token != Symbol.IDENT)
			error.signal("Expecting Identifier");
		if(lexer.nextToken() != Symbol.ASSIGN)
			error.signal("Expecting Assign signal");
		expr();
	}

	// read_stmt -> READ ( id_list );
	public void read_stmt(){
		if(lexer.token != Symbol.READ)
			error.signal("Missing READ keyword");
		if(lexer.nextToken() != Symbol.LPAR)
			error.signal("Missing open Parentheses");

		id_list();
		if(lexer.nextToken() != Symbol.RPAR)
			error.signal("Missing close Parentheses");
		if(lexer.nextToken() != Symbol.SEMICOLON)
			error.signal("Semicolon expected");
	}

	// write_stmt -> WRITE ( id_list );
 	public void write_stmt(){
		if(lexer.token != Symbol.WRITE)
			error.signal("Missing READ keyword");
		if(lexer.nextToken() != Symbol.LPAR)
			error.signal("Missing open Parentheses");
		id_list();
		if(lexer.nextToken() != Symbol.RPAR)
			error.signal("Missing close Parentheses");
		if(lexer.nextToken() != Symbol.SEMICOLON)
			error.signal("Semicolon expected");
 	}

	// return_stmt -> RETURN expr ;
	public void return_stmt(){
		if(lexer.token != Symbol.RETURN)
			error.signal("Missing RETURN keyword");
		lexer.nextToken();
		expr();
	}

	/***************/
	/* Expressions */
	/***************/

	// expr -> factor expr_tail
	public boolean expr(){
		if(factor())
			return expr_tail();
		return false;
	}

	// expr_tail -> addop factor expr_tail | empty
	public boolean expr_tail(){
		if(lexer.token == Symbol.PLUS){
			lexer.nextToken();
			if(factor())
				return expr_tail();
			else
				return false;
		}
		return true;
	}

	// factor -> postfix_expr factor_tail
	public boolean factor(){
		if(postfix_expr())
			return factor_tail();
		return false;
	}

	// factor_tail -> mulop postfix_expr factor_tail | empty
	public boolean factor_tail(){
		if(lexer.token == Symbol.MULT){
			if(postfix_expr())
				return factor_tail();
			else
				return false;
		}
		return true;
	}

	// postfix_expr -> primary | call_expr
	public boolean postfix_expr(){
		// ????????????????????????????????????????????????????????????????????????????????????????????????????
		// Verificar se não há ambiguidade do tipo ID (   onde o '('  não é do call_expr mas sim de outra coisa
		if(lexer.token == Symbol.IDENT){
			Symbol temp = lexer.checkNextToken();
			if(temp == Symbol.LPAR)
				return call_expr();
			else
				return primary();
		}
		else
			return primary();
	}


	// call_expr -> id ( {expr_list} )
	public boolean call_expr(){
		if(lexer.token != Symbol.IDENT)
			error.signal("Missing identificator on line" + lexer.getLineNumber());
		if(lexer.nextToken() != Symbol.LPAR)
			error.signal("Expecting ( on line"  + lexer.getLineNumber());
		return true;
	}


	// expr_list -> expr expr_list_tail
	public void expr_list(){
		expr();
		expr_list_tail();
	}


	// expr_list_tail -> , expr expr_list_tail | empty
	public void expr_list_tail(){
		if(lexer.token == Symbol.COMMA){
			expr();
			expr_list_tail();
		}
	}

	// primary -> (expr) | id | INTLITERAL | FLOATLITERAL
	public boolean primary(){
		// ????????????????????????????????????????????????????????????????????????????????????????????????????????
		if(!expr() && lexer.token != Symbol.IDENT && lexer.token != Symbol.INT && lexer.token != Symbol.FLOAT)
			error.signal("Not a primary element");
		lexer.nextToken();
		return true;
	}


 	// addop -> + | -
	public void addop(){
		if(lexer.token != Symbol.PLUS && lexer.token != Symbol.MINUS)
			error.signal("Wrong operator");
		lexer.nextToken();
	}

	// mulop -> * | /
	public void mulop(){
		if(lexer.token != Symbol.MULT && lexer.token != Symbol.DIV)
			error.signal("Wrong operator");
		lexer.nextToken();
	}

	/************************************/
	/* Complex Statements and Condition */
	/************************************/

	// if_stmt -> IF ( cond ) THEN stmt_list else_part ENDIF
	public void if_stmt(){
		if(lexer.token == Symbol.IF){

			if(lexer.nextToken() != Symbol.LPAR)
				error.signal("Missing parantheses");
			lexer.nextToken();

			cond();

			if(lexer.token != Symbol.RPAR)
				error.signal("Missing parantheses");

			if(lexer.nextToken() != Symbol.THEN)
				error.signal("Missing THEN keyword");

			lexer.nextToken();

			stmt_list();

			else_part();

			if(lexer.token != Symbol.ENDIF)
				error.signal("Missing ENDIF keyword");

			lexer.nextToken();

		} else
				error.signal("Missing IF keyword");
	}

	// else_part -> ELSE stmt_list | empty
	public void else_part(){
		if(lexer.token == Symbol.ELSE){
			lexer.nextToken();
			stmt_list();
		}
	}

	// cond -> expr compop expr
	public void cond(){
		expr();

		compop();

		expr();
	}

	// compop -> < | > | =
	public void compop(){
		if(lexer.token != Symbol.LT && lexer.token != Symbol.GT && lexer.token != Symbol.EQUAL)
			error.signal("Missing comparison operator");
		lexer.nextToken();
	}

	// for_stmt -> FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
	public void for_stmt(){
		if(lexer.token == Symbol.FOR){

			if(lexer.nextToken() != Symbol.LPAR)
				error.signal("Missing parantheses");

			lexer.nextToken();

			assign_expr();

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Missing end of declaration");
			lexer.nextToken();

			cond();

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Missing end of declaration");
			lexer.nextToken();

			assign_expr();

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Missing end of declaration");


			if(lexer.nextToken() != Symbol.RPAR)
				error.signal("Missing parantheses");
			lexer.nextToken();

			stmt_list();

			if(lexer.token != Symbol.ENDFOR)
				error.signal("Missing ENDFOR keyword");
			lexer.nextToken();

		} else
				error.signal("Missing FOR keyword");
	}


	private Lexer lexer;
  private CompilerError error;

}
