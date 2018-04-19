package compilador.AST;

import java.io.*;

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

  public void genC(){
    PW pw = PW.getPW();
    pw.print(this.type.toLowerCase()+" "+this.id.getId()+"(");
    if(this.pdl != null)
        this.pdl.genC();
    pw.rawPrint(")\n");
    pw.println("{");
    pw.add();
    
    this.fd.genC();
    pw.sub();
    pw.println("}");
  }
}
