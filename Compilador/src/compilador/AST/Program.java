package compilador.AST;

import java.lang.String;

public class Program {
    private Ident id;
    private ProgramBody pgm;
	public Program(Ident id, ProgramBody pgm ){
		this.id = id;
		this.pgm = pgm;
	}
	public void genC(){
            PW pw = PW.getPW();
            pw.setFileName(this.id.getId());
            pw.println("#include <stdio.h>;\n\n");
            this.pgm.genC();
            pw.close();
	}
}
