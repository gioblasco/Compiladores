package AST;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PW {

    private String fileName;

    public void add() {
        currentIndent++;
    }

    public void sub() {
        if (currentIndent > 0) {
            currentIndent--;
        }
    }

    private PW() {

    }

    public static PW getPW() {
        if (pw == null) {
            pw = new PW();
            pw.out = new PrintWriter(System.out);
        }
        return pw;
    }

    public void set(PrintWriter out) {
        this.out = out;
        currentIndent = 0;
    }

    public void set(int indent) {
        currentIndent = indent;
    }

    public int getIndent(){
        return currentIndent;
    }
    public void print(String s) {
        for (int c = 0; c < currentIndent; c++) {
            out.print("\t");
        }
        out.flush();
        out.print(s);

    }

    public void rawPrint(String s) {
        out.print(s);
    }

    public void println(String s) {
        pw.print(s + "\n");
    }

    public void close() {
        out.close();
    }

    public void setFileName(String id) throws FileNotFoundException, UnsupportedEncodingException {
        this.fileName = id;
        
        //PW.out = new PrintWriter( System.out);
        PW.out = new PrintWriter(this.fileName.concat(".c"), "UTF-8");
    }

    static int currentIndent = 0;
    /* there is a Java and a Green mode. 
      indent in Java mode:
      3 6 9 12 15 ...
      indent in Green mode:
      3 6 9 12 15 ...
     */
    static public final int green = 0, java = 1;
    int mode = green;
    public static PrintWriter out;
    // Uma única referência para a classe toda.
    private static PW pw = null;

}
