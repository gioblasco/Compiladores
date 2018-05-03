/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AST;

/**
 *
 * @author spectrus
 */
public class Type {
    private String type;
    private boolean function;

    public Type(String type, boolean isFunction) {
        this.type = type;
        this.function = isFunction;
    }
    

    public String getType() {
        return type;
    }

    public boolean isFunction() {
        return function;
    }
    
    
    
}
