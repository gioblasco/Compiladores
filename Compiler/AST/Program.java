package Compiler.AST;

import java.util.*;

public class Program {
	public Program(Ident id, PgmBody pgm ){
		this.id = id;
		this.pgm = pgm;
	}
	public void genC(PW pw){
		pw.setFileName(id);
		pw.println("#include <stdio.h>;");
		pw.println("\nint main (){");
		pw.add();
		this.pgm.genC();
		pw.println("return 0;");
		pw.sub();
		pw.println("}");
	}
}
