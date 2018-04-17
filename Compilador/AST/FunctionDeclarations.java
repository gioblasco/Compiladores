public class FunctionDeclarations{
  private ArrayList<FuncDecl> alfd;

  public FunctionDeclarations(ArrayList<FuncDecl> al){
    this.alfd = al;
  }

  public void genC(PW pw){
    for(FuncDecl x: this.alfd){
      x.genC();
    }
  }
}
