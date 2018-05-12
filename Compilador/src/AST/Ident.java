package AST;

public class Ident{
	private String id;

	public Ident(String id){
		this.id = id;
	}

	public String getName(){
		return this.id;
	}
	
}