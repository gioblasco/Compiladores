/*
    comp5
 Vamos aprimorar o analisador lexico. Na linguagem abaixo, temos palavras-chave com mais que um caracter, nomes de variaveis formado por 1 ou mais caracteres e numeros inteiros positivos. Note que nesta linguagem eh necessario declarar pelo menos uma variavel. Alem disso, esta linguagem aceita como comentario uma linha que inicia com //. 
 A mensagem de erro eh de responsabilidade da classe CompileError.
 O codigo fonte pode ser escrito em um arquivo separado.
 Utilize o GC para geracao de codigo em C e DEBUGLEXER para imprimir os tokens reconhecidos.
        
    Grammar:
       Program ::= 'begin' VarDecList ';' AssignStatement 'end'
       VarDecList ::= Variable | Variable ',' VarDecList
       Variable ::= Letter {Letter}
       Letter ::= 'A' | 'B' | ... | 'Z' | 'a' | ... |'z'
       AssignStatement ::= Variable '=' Expr ';'      
       Expr ::= Oper  Expr Expr  | Number | Variable
       Oper ::= '+' | '-' | '*' | '/'
       Number ::= Digit {Digit} 
       Digit ::= '0'| '1' | ... | '9'
*/

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
    
    //   Program ::= 'begin' VarDecList ';' AssignStatement 'end'
    public void program(){
        if(lexer.token != Symbol.BEGIN)
            error.signal("Missing BEGIN keyword");
        lexer.nextToken();
        
        varDecList();  

        if(lexer.token!= Symbol.END)
            error.signal("Missing END keyword");
        lexer.nextToken();
    }
       
	// VarDecList ::= Variable | Variable ',' VarDecList
	public void varDecList(){
        if(lexer.token != Symbol.IDENT)
            error.signal("Error! Variable identifier expected.");
        lexer.nextToken();
        if(lexer.token == Symbol.COMMA){
            lexer.nextToken();
            varDecList();
        }
            
	}

    //AssignStatement ::= Variable '=' Expr ';'
    public void assignStatement(){
        if(lexer.token != Symbol.IDENT){
            error.signal("Missing Var identifier");
        }
        lexer.nextToken();
        if(lexer.token != Symbol.ASSIGN){
            error.signal("Missing assign '=' signal");
        }

        expr();
        
        if(lexer.token != Symbol.SEMICOLON)
            error.signal("Missing ; on line X");
        lexer.nextToken();
    }
    
    // Expr ::= Oper  Expr Expr  | Number | Variable
    public void expr(){
        if(Oper()){
            lexer.nextToken();
            expr();
            expr();
        }
        else if(lexer.token == Symbol.IDENT || lexer.NUMBER)
            lexer.nextToken();
        else
            error.signal("Invalid syntax of Expression");
        
        
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
    
    
    
