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

  public void genC(PW pw){
    pw.print(this.type.toLowerCase()+" "+this.id.getId()+"(");
    this.pdl.genC(pw);
    pw.println("){");
    pw.add();
    this.fd.genC(pw);
    pw.sub();
    pw.println("}");
  }
}
