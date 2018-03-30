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
            error.signal("Not expected ''" + lexer.token + "' after END keyword (end of file).'" );
    }

	/***************************************/
	/************** Program ****************/
	/***************************************/

    //   program ::= PROGRAM id BEGIN pgm_body END
    public void program(){

        if(lexer.token != Symbol.PROGRAM)
            error.signal("Missing PROGRAM keyword at program()");

        lexer.nextToken();

        if(lexer.token!= Symbol.IDENT)
			error.signal("Missing PROGRAM Identifier at program()");

		lexer.nextToken();

		if(lexer.token != Symbol.BEGIN)
            error.signal("Missing BEGIN keyword at program()");

		lexer.nextToken();

		pgm_body();

        if(lexer.token!= Symbol.END)
            error.signal("Missing END keyword at program()");

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
				
				if(lexer.nextToken() != Symbol.IDENT)
					error.signal("Missing identifier in string declaration");
				
				if(lexer.nextToken() != Symbol.ASSIGN)
					error.signal("Missing assignment symbol");

				lexer.nextToken();
				
				if(lexer.token != Symbol.STRINGLITERAL)
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
				error.signal("Missing end of declaration at var_decl()");
			lexer.nextToken();
		}
	}

	// var_type -> FLOAT | INT
	public boolean var_type(){
		if(lexer.token != Symbol.FLOAT && lexer.token != Symbol.INT)
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
				error.signal("Missing identifier at id_tail()");
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
		if(!var_type())
			error.signal("Missing correct variable type at param_decl()");

		if(lexer.token != Symbol.IDENT)
			error.signal("Missing identifier at param_decl()");
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
				error.signal("Missing identifier at func_decl()");
			lexer.nextToken();

			if(lexer.token != Symbol.LPAR)
				error.signal("Missing parantheses at func_decl()");
			lexer.nextToken();

			if(lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT || lexer.token == Symbol.STRING)
				param_decl_list();

			if(lexer.token != Symbol.RPAR)
				error.signal("Missing parantheses at func_decl()");
			lexer.nextToken();

			if(lexer.token != Symbol.BEGIN)
				error.signal("Missing BEGIN keyword at func_decl()");
			lexer.nextToken();

			func_body();

			if(lexer.token != Symbol.END)
				error.signal("Missing END keyword at func_decl()");
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

	// stmt -> assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt | call_expr ;
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
				else if(temp == Symbol.LPAR){
					call_expr();
					if(lexer.token != Symbol.SEMICOLON)
						error.signal("Missing semicolon after call_expr() at stmt()");
					lexer.nextToken();
				}
				else
					error.signal("Wrong use of element after identifier at stmt()");
			} else
					error.signal("Wrong statement declaration at stmt()");
	}

	/********************/
	/* Basic Statements */
	/********************/

	// assign_stmt -> assign_expr ;
	public void assign_stmt(){
		assign_expr();
		if(lexer.token != Symbol.SEMICOLON)
			error.signal("Semicolon expected at assign_stmt()" + lexer.getStringValue());
		lexer.nextToken();
	}

	// assign_expr -> id := expr
	public void assign_expr(){
		if(lexer.token != Symbol.IDENT)
			error.signal("Expecting identifier at assign_expr()");
		if(lexer.nextToken() != Symbol.ASSIGN)
			error.signal("Expecting assign signal assign_expr()");
		lexer.nextToken();
		expr();
	}

	// read_stmt -> READ ( id_list );
	public void read_stmt(){
		if(lexer.token != Symbol.READ)
			error.signal("Missing READ keyword at read_stmt()");
		if(lexer.nextToken() != Symbol.LPAR)
			error.signal("Missing open parentheses at read_stmt()");
		lexer.nextToken();
		id_list();
		if(lexer.token != Symbol.RPAR)
			error.signal("Missing close parentheses at read_stmt()");
		if(lexer.nextToken() != Symbol.SEMICOLON)
			error.signal("Semicolon expected at read_stmt()");
		lexer.nextToken();
	}

	// write_stmt -> WRITE ( id_list );
 	public void write_stmt(){
		if(lexer.token != Symbol.WRITE)
			error.signal("Missing READ keyword at write_stmt()");
		if(lexer.nextToken() != Symbol.LPAR)
			error.signal("Missing open parentheses at write_stmt()");
		lexer.nextToken();
		id_list();
		if(lexer.token != Symbol.RPAR)
			error.signal("Missing close parentheses write_stmt()");
		if(lexer.nextToken() != Symbol.SEMICOLON)
			error.signal("Semicolon expected at write_stmt()");
		lexer.nextToken();
 	}

	// return_stmt -> RETURN expr ;
	public void return_stmt(){
		if(lexer.token != Symbol.RETURN)
			error.signal("Missing RETURN keyword at return_stmt()");
		lexer.nextToken();
		expr();
		if(lexer.token != Symbol.SEMICOLON)
			error.signal("Semicolon expected at return_stmt()");
		lexer.nextToken();
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
		if(lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS){
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
		if(lexer.token == Symbol.MULT || lexer.token == Symbol.DIV){
			lexer.nextToken();
			if(postfix_expr())
				return factor_tail();
			else
				return false;
		}
		return true;
	}

	// postfix_expr -> primary | call_expr
	public boolean postfix_expr(){
		if(lexer.token == Symbol.IDENT){
			Symbol temp = lexer.checkNextToken();
			if(temp == Symbol.LPAR){
				return call_expr();
			} else
				return primary();
		}
		else
			return primary();
	}


	// call_expr -> id ( {expr_list} )
	public boolean call_expr(){
		if(lexer.token != Symbol.IDENT)
			error.signal("Missing identifier at call_expr()");
		if(lexer.nextToken() != Symbol.LPAR)
			error.signal("Expecting begin parentheses at call_expr()");
		lexer.nextToken();
		if(lexer.token != Symbol.RPAR)
			expr_list();
		if(lexer.token != Symbol.RPAR)
			error.signal("Expecting close parentheses at call_expr()");
		lexer.nextToken();
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
			lexer.nextToken();
			expr();
			expr_list_tail();
		}
	}

	// primary -> (expr) | id | INTLITERAL | FLOATLITERAL
	public boolean primary(){
		if(lexer.token == Symbol.LPAR){
			lexer.nextToken();
			expr();
			if(lexer.token != Symbol.RPAR)
				error.signal("Missing close parentheses at primary()");
		} else if(lexer.token != Symbol.IDENT && lexer.token != Symbol.INTLITERAL && lexer.token != Symbol.FLOATLITERAL)
			error.signal("Not a primary element at primary()");
		lexer.nextToken();
		return true;
	}


 	// addop -> + | -
	public void addop(){
		if(lexer.token != Symbol.PLUS && lexer.token != Symbol.MINUS)
			error.signal("Wrong operator at addop()");
		lexer.nextToken();
	}

	// mulop -> * | /
	public void mulop(){
		if(lexer.token != Symbol.MULT && lexer.token != Symbol.DIV)
			error.signal("Wrong operator at mulop()");
		lexer.nextToken();
	}

	/************************************/
	/* Complex Statements and Condition */
	/************************************/

	// if_stmt -> IF ( cond ) THEN stmt_list else_part ENDIF
	public void if_stmt(){
		if(lexer.token == Symbol.IF){

			if(lexer.nextToken() != Symbol.LPAR)
				error.signal("Missing parantheses at if_stmt()");
			lexer.nextToken();

			cond();

			if(lexer.token != Symbol.RPAR)
				error.signal("Missing parantheses at if_stmt()");

			if(lexer.nextToken() != Symbol.THEN)
				error.signal("Missing THEN keyword at if_stmt()");

			lexer.nextToken();

			stmt_list();

			else_part();

			if(lexer.token != Symbol.ENDIF)
				error.signal("Missing ENDIF keyword at if_stmt()");

			lexer.nextToken();

		} else
				error.signal("Missing IF keyword at if_stmt()");
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
			error.signal("Missing comparison operator at compop()");
		lexer.nextToken();
	}

	// for_stmt -> FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
	public void for_stmt(){
		if(lexer.token == Symbol.FOR){
			lexer.printToken(564);

			if(lexer.nextToken() != Symbol.LPAR)
				error.signal("Missing parantheses at for_stmt()");
			lexer.printToken(568);
			
			lexer.nextToken();
			lexer.printToken(571);
			if(lexer.token != Symbol.SEMICOLON)
				assign_expr();
			lexer.printToken(573);

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Missing end of declaration at for_stmt()");
			lexer.printToken(577);
			
			if(lexer.nextToken() != Symbol.SEMICOLON)
				cond();
			lexer.printToken(581);

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Missing end of declaration at for_stmt()");
			lexer.printToken(585);
			if(lexer.nextToken() != Symbol.RPAR)
				assign_expr();
			lexer.printToken(588);

			if(lexer.token != Symbol.RPAR)
				error.signal("Missing parantheses at for_stmt()");
			lexer.nextToken();
			lexer.printToken(593);
			stmt_list();

			if(lexer.token != Symbol.ENDFOR)
				error.signal("Missing ENDFOR keyword at for_stmt()");
			lexer.nextToken();

		} else
				error.signal("Missing FOR keyword at for_stmt()");
	}


	private Lexer lexer;
  private CompilerError error;

}
