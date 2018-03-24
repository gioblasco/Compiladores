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
            error.signal("Error! Missing PROGRAM keyword on the line: " + lexer.getLineNumber());

        lexer.nextToken();

        if(lexer.token!= Symbol.IDENT)
			error.signal("Error! Missing PROGRAM Identifier on the line: " + lexer.getLineNumber());

		lexer.nextToken();

		if(lexer.token != Symbol.BEGIN)
            error.signal("Error! Missing BEGIN keyword on the line: " + lexer.getLineNumber());

		lexer.nextToken();

		pgm_body();

        if(lexer.token!= Symbol.END)
            error.signal("Missing BEGIN keyword");
        lexer.nextToken();
    }

	// pgm_body -> decl func_declarations
	public void pgm_body(){

	}
	// decl -> string_decl_list {decl} | var_decl_list {decl} | empty
	public void decl(){

	}

	/* Global String Declaration */
	//string_decl {string_decl_tail}
	public void string_decl_list(){

	}

	//STRING id := str ; | empty
	public void string_decl(){

	}

	//string_decl {string_decl_tail}
	public void string_decl_tail(){

	}

	/* Variable Declaration */
	//var_decl {var_decl_tail}
	public void var_decl_list(){

	}

	//var_type id_list ; | empty
	public void var_decl(){

	}

	//FLOAT | INT
	public void var_type(){

	}

	//var_type | VOID
	public void any_type(){

	}

	//id id_tail
	public void id_list(){

	}

	//, id id_tail | empty
	public void id_tail(){

	}

	//var_decl {var_decl_tail}
	public void var_decl_tail(){

	}

	/* Function Paramater List */
	//param_decl param_decl_tail
	public void param_decl_list(){

	}

	//var_type id
	public void param_decl(){

	}

	//, param_decl param_decl_tail | empty
	public void param_decl_tail(){

	}

	/* Function Declarations */
	//func_decl {func_decl_tail}
	public void func_declarations(){

	}

	//FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
	public void func_decl(){

	}

	//func_decl {func_decl_tail}
	public void func_decl_tail(){

	}

	//decl stmt_list
	public void func_body(){

	}

	/* Statement List */
	//stmt stmt_tail | empty
	public void stmt_list(){

	}

	//stmt stmt_tail | empty
	public void stmt_tail(){

	}

	//assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt
	public void stmt(){

	}

	/* Basic Statements */
	//assign_expr ;
	public void assign_stmt(){

	}

	//id := expr
	public void assign_expr(){

	}

	//READ ( id_list );
	public void read_stmt(){

	}

	//WRITE ( id_list );
 	public void write_stmt(){

 	}

	//RETURN expr ;
	public void return_stmt(){

	}

	/* Expressions */
	//factor expr_tail
	public void expr(){

	}

	//addop factor expr_tail | empty
	public void expr_tail(){

	}

	//postfix_expr factor_tail
	public void factor(){

	}

	//mulop postfix_expr factor_tail | empty
	public void factor_tail(){

	}

	//primary | call_expr
	public void postfix_expr(){

	}


	//id ( {expr_list} )
	public void call_expr(){

	}


	//expr expr_list_tail
	public void expr_list(){

	}


	//, expr expr_list_tail | empty
	public void expr_list_tail(){

	}

	//(expr) | id | INTLITERAL | FLOATLITERAL
	public void primary(){

	}


 	//+ | -
	public void addop(){

	}

	// * | /
	public void mulop(){

	}

	/* Complex Statements and Condition */
	//IF ( cond ) THEN stmt_list else_part ENDIF
	public void if_stmt(){

	}

	//ELSE stmt_list | empty
	public void else_part(){

	}

	//expr compop expr
	public void cond(){

	}

	//< | > | =
	public void compop(){

	}

	//FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
	public void for_stmt(){

	}

   /* public boolean Oper()
    {
        if(lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS || lexer.token == Symbol.DIV || lexer.token == Symbol.MULT)
            return true;
        return false;
    }
    */
	private Lexer lexer;
    private CompilerError error;

}
