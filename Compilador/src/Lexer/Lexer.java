package Lexer;

import Error.CompilerError;
import java.util.*;

public class Lexer {

	// apenas para verificacao lexica
	public static final boolean DEBUGLEXER = true;

	public Lexer(char[] input, CompilerError error) {
		this.input = input;
		// add an end-of-file label to make it easy to do the lexer
		input[input.length - 1] = '\0';
		// number of the current line
		lineNumber = 1;
		tokenPos = 0;
		this.error = error;
	}

	// contains the keywords - palavras reservadas da linguagem
	static private Hashtable < String,
	Symbol > keywordsTable;

	// this code will be executed only once for each program execution
	static {
		keywordsTable = new Hashtable < String,
		Symbol > ();
		keywordsTable.put("PROGRAM", Symbol.PROGRAM);
		keywordsTable.put("BEGIN", Symbol.BEGIN);
		keywordsTable.put("END", Symbol.END);
		keywordsTable.put("FUNCTION", Symbol.FUNCTION);
		keywordsTable.put("RETURN", Symbol.RETURN);
        keywordsTable.put("READ", Symbol.READ);
		keywordsTable.put("WRITE", Symbol.WRITE);
		keywordsTable.put("IF", Symbol.IF);
		keywordsTable.put("ELSE", Symbol.
		ELSE);
		keywordsTable.put("ENDIF", Symbol.ENDIF);
		keywordsTable.put("THEN", Symbol.THEN);
		keywordsTable.put("FOR", Symbol.FOR);
		keywordsTable.put("ENDFOR", Symbol.ENDFOR);
		keywordsTable.put("INT", Symbol.INT);
		keywordsTable.put("FLOAT", Symbol.FLOAT);
		keywordsTable.put("VOID", Symbol.VOID);
		keywordsTable.put("STRING", Symbol.STRING);
	}

	public Symbol checkNextToken() {
		int backupTokenPos = tokenPos;
		int backupLineNumber = lineNumber;
		Symbol backupToken = token;
		int backupIntValue = intValue;
		float backupFloatValue = floatValue;
		String backupStringValue = stringValue;

		Symbol retorno = nextToken();

		tokenPos = backupTokenPos;
		lineNumber = backupLineNumber;
		token = backupToken;
		intValue = backupIntValue;
		floatValue = backupFloatValue;
		stringValue = backupStringValue;

		return retorno;
	}

	public Symbol nextToken() {
		while (input[tokenPos] == ' ' || input[tokenPos] == '\n' || input[tokenPos] == '\t' || input[tokenPos] == '\r') {
			if (input[tokenPos] == '\n') {
				lineNumber++;
			}
			tokenPos++;
		}

		//chegou no final do arquivo
		if (input[tokenPos] == '\0') {
			token = Symbol.EOF;
			return token;
		}

		//verificar se é comentario
		if (input[tokenPos] == '-' && input[tokenPos + 1] == '-') {
			while (input[tokenPos] != '\n' && input[tokenPos] != '\0') {
				tokenPos++;
			}
			return nextToken();
		}

		// Checking if the token is a number
		String aux = "";
		boolean float_number = false;
		while (Character.isDigit(input[tokenPos]) || input[tokenPos] == '.') {
			if (input[tokenPos] == '.') {
				if (float_number == false) float_number = true;
				else error.signal("Error, number with invalid notation! Got two dots instead one.");
			}
			
			aux = aux.concat(Character.toString(input[tokenPos]));
			tokenPos++;
		}

		if (aux.length() > 0) {
			if (float_number == false) {
				//converte string para inteiro
				intValue = Integer.parseInt(aux);
                stringValue = String.valueOf(intValue);
				if (intValue > MaxValueInteger) {
					error.signal("Error: Not valid integer at line: " + lineNumber);
				}
				token = Symbol.INTLITERAL;
			}
			else {
				// Converte String para ponto flutuante.
				floatValue = Float.parseFloat(aux);
                stringValue = String.valueOf(floatValue);
				token = Symbol.FLOATLITERAL;
			}
		} else {
			while (Character.isLetter(input[tokenPos]) || Character.isDigit(input[tokenPos])) {
				aux = aux.concat(Character.toString(input[tokenPos]));
				tokenPos++;
			}
			if (aux.length() > 0) {
				Symbol temp;
				temp = keywordsTable.get(aux.toString());
                stringValue = aux.toString();
				if (temp == null) {
					if (aux.length() > 30) error.signal("Error, identifier size limit reached");
					token = Symbol.IDENT;
					
				}
				else {
					token = temp;
				}
			} else {
                stringValue = String.valueOf(input[tokenPos]);
				switch (input[tokenPos]) {
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
				case ':':
					if (input[tokenPos + 1] == '=') {
						tokenPos++;
						token = Symbol.ASSIGN;
					} else error.signal("Not expected : ");
					break;
				case '=':
					token = Symbol.EQUAL;
					break;
				case '>':
					token = Symbol.GT;
					break;
				case '<':
					token = Symbol.LT;
					break;
				case ',':
					token = Symbol.COMMA;
					break;
				case ';':
					token = Symbol.SEMICOLON;
					break;
				case '(':
					token = Symbol.LPAR;
					break;
				case ')':
					token = Symbol.RPAR;
					break;
				case '"':
					stringValue = "";
					tokenPos++;
					int i;
					for (i = 0; input[tokenPos] != '\0' && input[tokenPos] != '"' && i < 80; i++ ) {
						stringValue = stringValue.concat(Character.toString(input[tokenPos]));
						tokenPos++;
					}
					
					if (input[tokenPos] == '\0') 
						error.signal("Untermined String!");

					if(input[tokenPos]!= '"')
						error.signal("String size above limit of 80 chars");
					token = Symbol.STRINGLITERAL;
				
					break;

				default:
					error.signal("Lexical error at line: " + Integer.toString(lineNumber));
				}
				tokenPos++;
			}
		}

	
		lastTokenPos = tokenPos - 1;
		return token;
	}

	public void printToken() {
		System.out.println("O token é:" + token.toString());
	}
	public void printToken(int line) {
		System.out.println("O token da linha " + line + " é: " + token.toString());
	}
	// return the line number of the last token got with getToken()
	public int getLineNumber() {
		return lineNumber;
	}

	public String getCurrentLine() {
		int i = lastTokenPos;
		if (i == 0) i = 1;
		else if (i >= input.length) i = input.length;

		StringBuffer line = new StringBuffer();
		// go to the beginning of the line
		while (i >= 1 && input[i] != '\n')
                    i--;
		if (input[i] == '\n') i++;
		// go to the end of the line putting it in variable line
		while (input[i] != '\0' && input[i] != '\n' && input[i] != '\r') {
			line.append(input[i]);
			i++;
		}
		return line.toString();
	}

	public String getStringValue() {
		return stringValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public float getFloatValue() {
		return floatValue;
	}

	// current token
	public Symbol token;
	private String stringValue;
	private int intValue;
	private float floatValue;

	private int tokenPos;
	//  input[lastTokenPos] is the last character of the last token
	private int lastTokenPos;
	// program given as input - source code
	private char[] input;

	// number of current line. Starts with 1
	private int lineNumber;

	private CompilerError error;
	private static final int MaxValueInteger = 32768;
}