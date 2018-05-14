/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AST;

import java.util.ArrayList;

/**
 *
 * @author spectrus
 */
public class Type {
    private String type;
    private boolean function;
    private ArrayList<String> signature;

    public Type(String type, boolean isFunction) {
        this.type = type;
        this.function = isFunction;
    }
    
    public Type(String type, boolean isFunction, ArrayList<String> Signature) {
        this(type,isFunction);
        this.signature = Signature;
    } 

    public String getType() {
        return type;
    }

    public boolean isFunction() {
        return function;
    }
    
    public ArrayList<String> getSignature(){
        return this.signature;
    }
    
    
    
}
