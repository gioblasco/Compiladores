public class FunctionDeclarations{
  private ArrayList<FuncDecl> alfd;

  public FunctionDeclarations(ArrayList<FuncDecl> al){
    this.alfd = al;
  }

  public genC(){
    for(FuncDecl x: this.alfd){
      x.genC();
    }
  }
}
