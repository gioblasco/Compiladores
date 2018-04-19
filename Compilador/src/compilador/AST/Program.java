package compilador.AST;

import java.lang.String;

public class Program {
    private Ident id;
    private ProgramBody pgm;
	public Program(Ident id, ProgramBody pgm ){
		this.id = id;
		this.pgm = pgm;
	}
	public void genC(PW pw){
		pw.setFileName(id);
		pw.println("#include <stdio.h>;");
		pw.println("\nint main (){");
		pw.add();
		this.pgm.genC(pw);
		pw.println("return 0;");
		pw.sub();
		pw.println("}");
	}
}
