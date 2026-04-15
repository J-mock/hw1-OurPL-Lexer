package cpsc326;

public class Main {
    
    
    public static void main(String args[]){

        String arg = "(3 + \"true\") * 2";
        System.out.println("Trying lexer load");
        Lexer newLexer = new Lexer(arg);
        System.out.println("Tryig parse init and scan tokens");
        Parser newParser = new Parser(newLexer.scanTokens());
        System.out.println("Tokens scanned: " + newLexer.tokens);
        System.out.println("Trying parse");
        Expr parsedExpr = newParser.parse();
        
        System.out.println("Trying printer");
        ASTPrinter printer = new ASTPrinter();
        String output = printer.print(parsedExpr);

        System.out.println(output);
        Interpreter intr = new Interpreter();

        intr.interpret(parsedExpr);

        System.out.println(output);
        System.out.println("End");
    }
}
