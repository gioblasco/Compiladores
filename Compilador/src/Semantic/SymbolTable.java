package Semantic;


import java.util.*;

public class SymbolTable {
    
    public SymbolTable() {
        globalTable = new Hashtable<String,String>();
        localTable  = new Hashtable<String,String>();
    }
    
    public String putInGlobal( String key, String value ) {
       return globalTable.put(key, value);
    }

    public String putInLocal( String key, String value ) {
       return localTable.put(key, value);
    }
    
    public String getInLocal( String key ) {
       return localTable.get(key);
    }
    
    public String getInGlobal( String key ) {
       return globalTable.get(key);
    }
    
    public String get( String key ) {
        // returns the object corresponding to the key. 
        String result;
        if ( (result = localTable.get(key)) != null ) {
              // found local identifier
            return result;
        }
        else {
              // global identifier, if it is in globalTable
            return globalTable.get(key);
        }
    }

    public void removeLocalIdent() {
           // remove all local identifiers from the table
         localTable.clear();
    }
      
        
    private Hashtable<String,String> globalTable, localTable;
}
            