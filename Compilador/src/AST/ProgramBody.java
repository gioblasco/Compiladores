package AST;

public class ProgramBody {
	private Declaration dec;
	private FunctionDeclarations funcdec;

	public ProgramBody(Declaration d, FunctionDeclarations f){
		this.dec = d;
		this.funcdec = f;
	}

	public void genC(){
                if(this.dec != null)
                    this.dec.genC();
                if(this.funcdec != null)
                    this.funcdec.genC();
	}
}
