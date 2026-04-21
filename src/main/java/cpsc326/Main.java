package cpsc326;

import java.util.List;

public class Main {
    
    
    public static void main(String args[]){

        String source = "var value = 1; value = value + 2; print value;";
        Lexer lex = new Lexer(source);
        System.out.println("Tokens scanned: " + lex.scanTokens());
        
        Parser parse = new Parser(lex.scanTokens());
        ASTPrinter printer = new ASTPrinter();

        Interpreter interpret = new Interpreter();
        List<Stmt> stmts = parse.parse();
        
        
        
    }
}
