public class ProgramBody {
	private Declaration dec;
	private FunctionDeclarations funcdec;

	public ProgramBody(Declaration d, FunctionDeclarations f){
		this.dec = d;
		this.funcdec = f;
	}

	public void genC(PW pw){
		this.dec.genC();
		this.funcdec.genC();
	}
}
