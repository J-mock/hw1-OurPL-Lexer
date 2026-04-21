package cpsc326;

import java.util.HashMap;
import java.util.Map;

class Environment {
    Environment enclosing;
    Map<String, Object> values;
    

    // Empty constructor
    public Environment() {
        this.enclosing = null;
        this.values = new HashMap<>();
    }

    // Paramaterized constructor
    public Environment(Environment enclosing){
        this.enclosing = enclosing;
        this.values = new HashMap<>();
    }

    // Passing in a hash map too?
    public Environment(Environment enclosing, Map<String, Object> values) {
        this.enclosing = enclosing;
        this.values = values;
    }
    
    public void define(String str, Object obj){
        // Check here if already defined?
        // Allowing for other declaration?
        // var a = 5; var a = 6;  is ok?
        values.put(str, obj);
    }

    public Object get(Token token){
        // Void function?
        // Boolean?
        // Token?
        if(values.containsKey(token.lexeme)){
            return values.get(token.lexeme);
        }
        
        throw new RuntimeError(token, "Variable undefined");
    }
    
    public void assign(Token token, Object value){
        // Void function?
        // Assign different from define? Reassign variables
        // Variable exists but needs redefinition?
        if(values.containsKey(token.lexeme)){
            values.replace(token.lexeme, value);

            return;
        }
        // Need to check enclosing env
        if(enclosing != null){
            enclosing.assign(token, value);
            return;
        }

        // Not found in enclosing or anything up the tree
        // Throw error 
        throw new RuntimeError(token, "Undefined variable found... needs declaration");
    }
}
