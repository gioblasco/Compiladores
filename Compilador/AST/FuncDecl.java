public class FuncDecl{
  private String type;
  private Ident id;
  private ParamDeclList pdl;
  private FuncBody fd;

  public FuncDecl(String t, Ident i, ParamDeclList p, FuncBody f){
    this.type = t;
    this.id = i;
    this.pdl = p;
    this.fd = f;
  }

  public genC(){
    pw.print(this.type.toLower()+" "+this.id.getId()+"(");
    this.p.genC();
    pw.println("){");
    pw.add();
    this.f.genC();
    pw.sub();
    pw.println("}")
  }
}
