import Lexer.*;
import Error.*;

public class Compiler {

	// para geracao de codigo
	public static final boolean GC = false;

    public void compile( char []p_input ) {
        lexer = new Lexer(p_input, error);
        lexer.nextToken();
        program();
        if(lexer.token!= Symbol.EOF)
            error.signal("Not expected ''" + lexer.token + "' after END keyword.'" );
    }

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

		lexer.nextToken();
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

	/* Global String Declaration */
	// string_decl_list -> string_decl {string_decl_tail}
	public void string_decl_list(){
		string_decl();

		string_decl_tail();
	}

	// string_decl -> STRING id := str ; | empty
	public void string_decl(){
			if(lexer.token == Symbol.STRING){
				lexer.nextToken();

				if(lexer.token != Symbol.IDENT)
					error.signal("Error: Missing identifier at line: " + lexer.getLineNumber());
				lexer.nextToken();

				if(lexer.token != Symbol.ASSIGN)
					error.signal("Error: Missing assignment symbol at line: " + lexer.getLineNumber());
				lexer.nextToken();

				if(lexer.token != Symbol.STRING)
					error.signal("Error: Missing STRINGLITERAL type at line: " + lexer.getLineNumber());
				lexer.nextToken();

				if(lexer.token != Symbol.SEMICOLON)
					error.signal("Error: Missing end of declaration at line: " + lexer.getLineNumber());
				lexer.nextToken();

			}
	}

	// string_decl_tail -> string_decl {string_decl_tail}
	public void string_decl_tail(){
		string_decl();

		string_decl_tail();
	}

	/* Variable Declaration */
	// var_decl_list -> var_decl {var_decl_tail}
	public void var_decl_list(){
		var_decl();

		var_decl_tail();
	}

	// var_decl -> var_type id_list ; | empty
	public void var_decl(){
		if(lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_type();
			id_list();
			if(lexer.token == Symbol.SEMICOLON)
				error.signal("Error: Missing end of declaration at line: " + lexer.getLineNumber());
			lexer.nextToken();
		}
	}

	// var_type -> FLOAT | INT
	public void var_type(){
		if(lexer.token != Symbol.FLOAT && lexer.token == Symbol.INT)
			error.signal("Error: Wrong variable type at line: " + lexer.getLineNumber());
		lexer.nextToken();
	}

	// any_type -> var_type | VOID
	public void any_type(){
		if(lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT)
			var_type();
		else if(lexer.token != Symbol.VOID)
			error.signal("Error: Wrong type at line: " + lexer.getLineNumber());
		lexer.nextToken();
	}

	// id_list -> id id_tail
	public void id_list(){
		if(lexer.token == Symbol.IDENT){
			lexer.nextToken();
			id_tail();
		} else
				error.signal("Error: Wrong id_list declaration at line: " + lexer.getLineNumber());
		lexer.nextToken();
	}

	// id_tail -> , id id_tail | empty
	public void id_tail(){
		if(lexer.token == Symbol.COMMA){
			lexer.nextToken();

			if(lexer.token != Symbol.IDENT)
				error.signal("Error: Missing identifier at line: " + lexer.getLineNumber());
			lexer.nextToken();

			id_tail();
		}
	}

	// var_decl_tail -> var_decl {var_decl_tail}
	public void var_decl_tail(){
		var_decl();

		var_decl_tail();
	}

	/* Function Paramater List */
	// param_decl_list -> param_decl param_decl_tail
	public void param_decl_list(){
		param_decl();

		param_decl_tail();
	}

	// param_decl -> var_type id
	public void param_decl(){
		if(lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_type();

			if(lexer.token != Symbol.IDENT)
				error.signal("Error: Missing identifier at line: " + lexer.getLineNumber());
			lexer.nextToken();
		}
	}

	// param_decl_tail -> , param_decl param_decl_tail | empty
	public void param_decl_tail(){
		if(lexer.token == Symbol.COMMA){
				lexer.nextToken();

				param_decl();

				param_decl_tail();
		}
	}

	/* Function Declarations */
	// func_declarations -> func_decl {func_decl_tail}
	public void func_declarations(){
		func_decl();

		func_decl_tail();
	}

	// func_decl -> FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
	public void func_decl(){
		if(lexer.token == Symbol.FUNCTION){
			lexer.nextToken();

			any_type();

			if(lexer.token != Symbol.IDENT)
				error.signal("Error: Missing identifier at line: " + lexer.getLineNumber());
			lexer.nextToken();

			if(lexer.token != Symbol.LPAR)
				error.signal("Error: Missing parantheses at line: " + lexer.getLineNumber());
			lexer.nextToken();

			//NÃO SEI FAZER WHILE PRO param_decl_list (talvez uma condição de parada seja == ")", mas se n tiver ")" entra em loop infinito)
			param_decl_list();

			if(lexer.token != Symbol.RPAR)
				error.signal("Error: Missing parantheses at line: " + lexer.getLineNumber());
			lexer.nextToken();

			if(lexer.token != Symbol.BEGIN)
				error.signal("Error: Missing BEGIN keyword at line: " + lexer.getLineNumber());
			lexer.nextToken();

			func_body();

			if(lexer.token != Symbol.END)
				error.signal("Error: Missing END keyword at line: " + lexer.getLineNumber());
			lexer.nextToken();
		}
	}

	// func_decl_tail -> func_decl {func_decl_tail}
	public void func_decl_tail(){
		func_decl();

		func_decl_tail();
	}

	// func_body -> decl stmt_list
	public void func_body(){
		decl();

		stmt_list();
	}

	/* Statement List */
	// stmt_list -> stmt stmt_tail | empty
	public void stmt_list(){

	}

	// stmt_tail -> stmt stmt_tail | empty
	public void stmt_tail(){

	}

	// stmt -> assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt
	public void stmt(){

	}

	/* Basic Statements */
	// assign_stmt -> assign_expr ;
	public void assign_stmt(){

	}

	// assign_expr -> id := expr
	public void assign_expr(){

	}

	// read_stmt -> READ ( id_list );
	public void read_stmt(){

	}

	// write_stmt -> WRITE ( id_list );
 	public void write_stmt(){

 	}

	// return_stmt -> RETURN expr ;
	public void return_stmt(){

	}

	/* Expressions */
	// expr -> factor expr_tail
	public void expr(){

	}

	// expr_tail -> addop factor expr_tail | empty
	public void expr_tail(){

	}

	// factor -> postfix_expr factor_tail
	public void factor(){

	}

	// factor_tail -> mulop postfix_expr factor_tail | empty
	public void factor_tail(){

	}

	// postfix_expr -> primary | call_expr
	public void postfix_expr(){

	}


	// call_expr -> id ( {expr_list} )
	public void call_expr(){

	}


	// expr_list -> expr expr_list_tail
	public void expr_list(){

	}


	// expr_list_tail -> , expr expr_list_tail | empty
	public void expr_list_tail(){

	}

	// primary -> (expr) | id | INTLITERAL | FLOATLITERAL
	public void primary(){

	}


 	// addop -> + | -
	public void addop(){
		if(lexer.token != Symbol.PLUS && lexer.token != Symbol.MINUS)
			error.signal("Error: Wrong operator at line: " + lexer.getLineNumber());
		lexer.nextToken();
	}

	// mulop -> * | /
	public void mulop(){
		if(lexer.token != Symbol.MULT && lexer.token != Symbol.DIV)
			error.signal("Error: Wrong operator at line: " + lexer.getLineNumber());
		lexer.nextToken();
	}

	/* Complex Statements and Condition */
	// if_stmt -> IF ( cond ) THEN stmt_list else_part ENDIF
	public void if_stmt(){
		if(lexer.token == Symbol.IF){
			lexer.nextToken();

			if(lexer.token != Symbol.LPAR)
				error.signal("Error: Missing parantheses at line: " + lexer.getLineNumber());
			lexer.nextToken();

			cond();

			if(lexer.token != Symbol.RPAR)
				error.signal("Error: Missing parantheses at line: " + lexer.getLineNumber());
			lexer.nextToken();

			if(lexer.token != Symbol.THEN)
				error.signal("Error: Missing THEN keyword at line: " + lexer.getLineNumber());
			lexer.nextToken();

			stmt_list();

			else_part();

			if(lexer.token != Symbol.ENDIF)
				error.signal("Error: Missing ENDIF keyword at line: " + lexer.getLineNumber());
			lexer.nextToken();

		} else
				error.signal("Error: Missing IF keyword at line: " + lexer.getLineNumber());
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
			error.signal("Error: Missing comparison operator at line: " + lexer.getLineNumber());
		lexer.nextToken();
	}

	// for_stmt -> FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
	public void for_stmt(){
		if(lexer.token == Symbol.FOR){
			lexer.nextToken();

			if(lexer.token != Symbol.LPAR)
				error.signal("Error: Missing parantheses at line: " + lexer.getLineNumber());
			lexer.nextToken();

			assign_expr();

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Error: Missing end of declaration at line: " + lexer.getLineNumber());
			lexer.nextToken();

			cond();

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Error: Missing end of declaration at line: " + lexer.getLineNumber());
			lexer.nextToken();

			assign_expr();

			if(lexer.token != Symbol.SEMICOLON)
				error.signal("Error: Missing end of declaration at line: " + lexer.getLineNumber());
			lexer.nextToken();

			if(lexer.token != Symbol.RPAR)
				error.signal("Error: Missing parantheses at line: " + lexer.getLineNumber());
			lexer.nextToken();

			stmt_list();

			if(lexer.token != Symbol.ENDFOR)
				error.signal("Error: Missing ENDFOR keyword at line: " + lexer.getLineNumber());
			lexer.nextToken();
			
		} else
				error.signal("Error: Missing FOR keyword at line: " + lexer.getLineNumber());
	}


	private Lexer lexer;
  private CompilerError error;

}
