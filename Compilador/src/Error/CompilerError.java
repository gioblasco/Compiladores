package Error;

import Lexer.Lexer;

public class CompilerError {

    public CompilerError( Lexer lexer ) {
          // output of an error is done in out
        this.lexer = lexer;
        thereWasAnError = false;
    }

    public void setLexer( Lexer lexer ) {
        this.lexer = lexer;
    }

    public boolean wasAnErrorSignalled() {
        return thereWasAnError;
    }

    public void show( String strMessage ) {
        show( strMessage, false );
    }

    public void show( String strMessage, boolean goPreviousToken ) {
        // is goPreviousToken is true, the error is signalled at the line of the
        // previous token, not the last one.
        if ( goPreviousToken ) {
          System.out.println(ANSI_RED + "Error! at line " + lexer.getCurrentLine() + ": "+ ANSI_RESET);
        }
        else {
          System.out.println(ANSI_RED + "Error! at line " + lexer.getLineNumber() +": " + ANSI_RESET);
          System.out.println(ANSI_RED + lexer.getCurrentLine() + ANSI_RESET);
        }

        System.out.println(ANSI_RED + strMessage + "\n" +ANSI_RESET);

        thereWasAnError = true;
    }
    
    public void warning(String strMessage){
        
        //the program doesn't end, only shows warning
        
        System.out.println(ANSI_YELLOW + "Warning! at line " + lexer.getLineNumber() +": " + ANSI_RESET);
        System.out.println(ANSI_YELLOW + lexer.getCurrentLine() + ANSI_RESET);
          
        System.out.println(ANSI_YELLOW + strMessage + "\n" + ANSI_RESET);          
    }

    public void signal( String strMessage ) {
        show( strMessage );
        thereWasAnError = true;
        throw new RuntimeException("The program presented compilation error(s)");
    }

    private Lexer lexer;
    private boolean thereWasAnError;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
}
