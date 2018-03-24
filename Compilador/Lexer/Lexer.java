/*
 Program ::= 'begin' VarDecList ';' AssignStatment 'end'
 VarDecList ::= Variable | Variable ',' VarDecList
 Variable ::= Letter {Letter}
 Letter ::= 'A' | 'B' | ... | 'Z' | 'a' | ... |'z'
 AssignStatment ::= Variable '=' Expr ';'
 Expr ::= Oper  Expr Expr  | Number
 Oper ::= '+' | '-' | '*' | '/'
 Number ::= Digit {Digit}
 Digit ::= '0'| '1' | ... | '9'
 */

package Lexer;

import java.util.*;
import Error.*;

public class Lexer {

	// apenas para verificacao lexica
	public static final boolean DEBUGLEXER = true; 
    
    public Lexer( char []input, CompilerError error ) {
        this.input = input;
        // add an end-of-file label to make it easy to do the lexer
        input[input.length - 1] = '\0';
        // number of the current line
        lineNumber = 1; 
        tokenPos = 0;
        this.error = error;
    }
    
    // contains the keywords - palavras reservadas da linguagem
    static private Hashtable<String, Symbol> keywordsTable;
    
    // this code will be executed only once for each program execution
    static {
        keywordsTable = new Hashtable<String, Symbol>();
        keywordsTable.put( "begin", Symbol.BEGIN );
        keywordsTable.put( "end", Symbol.END );
    }
    
    
    public void nextToken() {
        while(input[tokenPos] == ' ' ||
                input[tokenPos] == '\n' || input[tokenPos] == '\t' ){
                if (input[tokenPos] == '\n'){
                    lineNumber++;
                }
                tokenPos++;
        }
        
        if (input[tokenPos] == '\0'){ //chegou no final do arq
            token = Symbol.EOF;
            return;
        }
        
        //verificar se eh comentario
        if (input[tokenPos] == '/' && input[tokenPos+1] == '/'){
            while(input[tokenPos] != '\n' && input[tokenPos] != '\0'){
                tokenPos++;
            }
            nextToken(); //importante
            return;
        }

        //quero reconhecer o token
        //o token sera um dos Symbol
        StringBuffer aux = new StringBuffer();
        while (Character.isDigit(input[tokenPos])){
            //concatenar esses digitos e concatenar eles
            aux = aux.append(input[tokenPos]); //vai concatenando o numero, ainda eh string
            tokenPos++;
        }

    
        if (aux.length() > 0){
            //converte string para inteiro
            numberValue = Integer.parseInt(aux.toString());
            if (numberValue > MaxValueInteger){
                error.signal("estourou o numero int");
            }
            token = Symbol.NUMBER;

        } else {
            while (Character.isLetter(input[tokenPos])){
                aux = aux.append(input[tokenPos]); //vai concatenando todas as letras, ainda eh string
                tokenPos++;
            }

            if (aux.length() > 0){
                Symbol temp;
                temp = keywordsTable.get(aux.toString()); //verifica na key word hash
                if (temp == null){ //nao eh palavra
                    token = Symbol.IDENT;
                    stringValue = aux.toString();
                }
                else {
                    token = temp;
                }
            } else {
                switch (input[tokenPos]){
                    case '+':
                        token = Symbol.PLUS;
                        break;
                    case '-':
                        token = Symbol.MINUS;
                        break;
                    case '/':
                        token = Symbol.DIV;
                        break;
                    case '*':
                        token = Symbol.MULT;
                        break;
                    case '=':
                        token = Symbol.ASSIGN;
                        break;
                    case ',':
                        token = Symbol.COMMA;
                        break;
                    case ';':
                        token = Symbol.SEMICOLON;
                        break;
                    default:
                        error.signal("erro lexico");
                }
                tokenPos++;
            }
        }

        


		if (DEBUGLEXER)
			System.out.println(token.toString());
        lastTokenPos = tokenPos - 1;
    }
    
    // return the line number of the last token got with getToken()
    public int getLineNumber() {
        return lineNumber;
    }
    
    public String getCurrentLine() {
        int i = lastTokenPos;
        if ( i == 0 )
            i = 1;
        else
            if ( i >= input.length )
                i = input.length;
        
        StringBuffer line = new StringBuffer();
        // go to the beginning of the line
        while ( i >= 1 && input[i] != '\n' )
            i--;
        if ( input[i] == '\n' )
            i++;
        // go to the end of the line putting it in variable line
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }
        return line.toString();
    }
    
    public String getStringValue() {
        return stringValue;
    }
    
    public int getNumberValue() {
        return numberValue;
    }
    
    public char getCharValue() {
        return charValue;
    }
    // current token
    public Symbol token;
    private String stringValue;
    private int numberValue;
    private char charValue;
    
    private int  tokenPos;
    //  input[lastTokenPos] is the last character of the last token
    private int lastTokenPos;
    // program given as input - source code
    private char []input;
    
    // number of current line. Starts with 1
    private int lineNumber;
    
    private CompilerError error;
    private static final int MaxValueInteger = 32768;
}
