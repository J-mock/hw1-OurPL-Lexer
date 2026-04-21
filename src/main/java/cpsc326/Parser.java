package cpsc326;

import java.util.ArrayList;
import java.util.List;

import static cpsc326.TokenType.AND;
import static cpsc326.TokenType.BANG;
import static cpsc326.TokenType.BANG_EQUAL;
import static cpsc326.TokenType.ELSE;
import static cpsc326.TokenType.EOF;
import static cpsc326.TokenType.EQUAL;
import static cpsc326.TokenType.EQUAL_EQUAL;
import static cpsc326.TokenType.FOR;
import static cpsc326.TokenType.FUN;
import static cpsc326.TokenType.GREATER;
import static cpsc326.TokenType.GREATER_EQUAL;
import static cpsc326.TokenType.IDENTIFIER;
import static cpsc326.TokenType.IF;
import static cpsc326.TokenType.LEFT_BRACE;
import static cpsc326.TokenType.LEFT_PAREN;
import static cpsc326.TokenType.LESS;
import static cpsc326.TokenType.LESS_EQUAL;
import static cpsc326.TokenType.MINUS;
import static cpsc326.TokenType.NUMBER;
import static cpsc326.TokenType.OR;
import static cpsc326.TokenType.PLUS;
import static cpsc326.TokenType.PRINT;
import static cpsc326.TokenType.RETURN;
import static cpsc326.TokenType.RIGHT_BRACE;
import static cpsc326.TokenType.RIGHT_PAREN;
import static cpsc326.TokenType.SEMICOLON;
import static cpsc326.TokenType.SLASH;
import static cpsc326.TokenType.STAR;
import static cpsc326.TokenType.STRING;
import static cpsc326.TokenType.STRUCT;
import static cpsc326.TokenType.VAR;
import static cpsc326.TokenType.WHILE;

class Parser {
    private static class ParseError extends RuntimeException{ }
    private ASTPrinter printer = new ASTPrinter();      // Printer for debugging purposes
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // From mini test, this needs to return a list of statements
    List<Stmt> parse() {
        List<Stmt> stmts = new ArrayList<>();

        while(!isAtEnd()){
            try {
                stmts.add(declaration());
                //consume(SEMICOLON, "Expected semicolon after declaration");
            } catch (ParseError e) {
                stmts.add(null);
                return stmts;
            }
            
        }
        return stmts;
        
    }

    private Stmt block(){
        // Already matched on the left brace
        List<Stmt> stmts = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd()){
            stmts.add(declaration());
        }
        consume(RIGHT_BRACE, "Expected closing brace after block");

        return new Stmt.Block(stmts);
    }

    private Stmt declaration(){
        if(match(VAR)){
            //System.out.println("Creating a var statement...");
            return varDeclaration();
        }
        return statement();
    }

    private Stmt varDeclaration(){
        consume(IDENTIFIER, "Expected variable name");
        Token name = previous();
        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
            //System.out.println(printer.print(initializer));
            // System.out.println("Found IDENTIFIER and EQUAL, initializer created");
        }
        if(initializer == null){
            error(previous(), "invalid variable declaration");
            return null;
        }
        consume(SEMICOLON, "Semicolon expected after variable declaration");
        //System.out.println("Var statement: " + name.lexeme);
        return new Stmt.Var(name, initializer); 
    }

    private Stmt statement(){
        if(match(PRINT)){
            
            return printStmt();
        }
        if(match(WHILE)){
            System.out.println("Creating a while statement");
            return whileStmt();
        }
        if(match(FOR)){
            return forStatement();
        }
        if(match(IF)){
            return ifStatement();
        }
        if(match(LEFT_BRACE)){
            // TEST -- where to consume closing brace
            // consume(RIGHT_BRACE, "Expecting closing brace for block");
            return block();
        }
        return exprStmt();
    }

    private Stmt ifStatement(){
        consume(LEFT_PAREN, "Left Paren expected after \"if\" declaration");
        Expr expr = expression();
        consume(RIGHT_PAREN, "Closing paren expected");
        Stmt left = statement();
        // Think this will create a block so no need to clean up RIGHT_BRACE HERE
        
        // Else should belong to most recent if statement, not sure if this should go here or somewhere elses
        if(match(ELSE)){
            return new Stmt.If(expr, left, statement());
        }
        return new Stmt.If(expr, left, null);
    }

    /* For statement */
    private Stmt forStatement(){
        consume(LEFT_PAREN, "Expected left paren after \"for\" declaration");
        // Few cases to check

        Stmt definition;
        Expr condition;
        Expr update;

        if(match(VAR)){
            definition = varDeclaration();
        }else if(match(SEMICOLON)){
            definition = null;
        }else{
            definition = exprStmt();
        }

        if(match(SEMICOLON)){
            condition = null;
        }else{
            condition = expression();
        }

        if(match(RIGHT_PAREN)){
            update = null;
        }else{
            update = expression();
            consume(RIGHT_PAREN, "Expected closing paren after for loop declaration");
        }


        return null;
    }

    private Stmt whileStmt(){
        
        consume(LEFT_PAREN, "Expected opening paren after while");
        //System.out.println(tokens.get(current).lexeme);
        Expr expr = expression();
        // Do not reach here?
        //System.out.println("CREATED EXPRESSSIOn");
        consume(RIGHT_PAREN, "Expected closing paren after while");
        Stmt stmt = statement();

        return new Stmt.While(expr, stmt);
    }

    

    private Stmt exprStmt(){
        Stmt expr = new Stmt.Expression(expression());
        consume(SEMICOLON, "Expected semicolon after expression statement");
        return expr;
    }

    private Stmt printStmt(){
        Stmt print = new Stmt.Print(expression());
        consume(SEMICOLON, "Expected semicolon after expression statement");
        return print;
    }

    private Expr expression() {
        Expr exprA = assignment();
        
        //System.out.println(printer.print(exprA));
        return exprA;
    }

    private Expr assignment(){
        // Might need to return an error, if we see an identifier, in this case it should always be assignment?
        // How do we handle associativity
        // System.out.println("Reached assignment for while test");

        if(match(IDENTIFIER)){
            //System.out.print("Matched identifer");
            Token name = previous();
            if(match(EQUAL)){
                //System.out.println(" Creating an assignment");
                Expr value = assignment();
                return new Expr.Assign(name, value);
            }else{
                // matched on identifier but need to go back
                current = current - 1;
            }
        }
        
        return logicOr();
    }

    private Expr logicOr(){
        Expr left = logicAnd();

        while(match(OR)){
            Token operator = previous();
            Expr right = logicAnd();
            left = new Expr.Logical(left, operator, right);
        }

        return left;
    }

    private Expr logicAnd(){
        Expr left = equality();

        while(match(AND)){
            Token operator = previous();
            Expr right = equality();
            left = new Expr.Logical(left, operator, right);
        }

        return left;
    }

    private Expr equality() {
        // Search first
        Expr left = comparison();
        // After comparison unfolds, current can either be eof or != ==
        while(match(BANG_EQUAL, EQUAL_EQUAL)){
  
            Token eqToken = previous();
            Expr right = comparison();
            left = new Expr.Binary(left, eqToken, right);
        }
        // Left will either be the full binary string or jsut whatever was searched in orginal left to beigin with

        return left;
    }

    private Expr comparison() {
        Expr left = term();
        while(match(GREATER, GREATER_EQUAL, LESS_EQUAL, LESS)){
            // Create another binary operator, left is already built up
            Token compToken = previous();
            Expr right = term();
            left = new Expr.Binary(left, compToken, right);
        }

        return left;
    }

    private Expr term() {
        Expr left = factor();
        while(match(PLUS, MINUS)){
            Token termToken = previous();
            Expr right = factor();
            left = new Expr.Binary(left, termToken, right);
        }
        return left;
    }

    private Expr factor() {
        Expr left = unary();
        while(match(STAR, SLASH)){
            Token factorToken = previous();
            Expr right = unary();
            left = new Expr.Binary(left, factorToken, right);
        }

        return left;
        
    }

    private Expr unary() {
        if(match(BANG, MINUS)){
            Token unaryToken = tokens.get(current - 1);
            Expr unaryExpr = new Expr.Unary(unaryToken, unary());
            return unaryExpr;
        }
        
        return primary();
    }

    private Expr primary() {
        
        switch(peek().type){
            case NUMBER:
            case STRING:
                advance();
                return new Expr.Literal(previous().literal);
            case TRUE:
            case FALSE:
                advance();
                return new Expr.Literal(previous().lexeme);
            case NIL:
                advance();
                return new Expr.Literal(previous().lexeme);
            case LEFT_PAREN:
                advance();
                Expr expr = new Expr.Grouping(expression());
                consume(RIGHT_PAREN, "Right parentheses expected");
                return expr;
            case IDENTIFIER:
                // Vairable could be created in assignment?
                advance();
                
                Expr exprV = new Expr.Variable(previous());
                
                return exprV;
            default:
                // Throw an error but recover
                // really not sure what to do with this
                synchronize();
                return new Expr.Literal(null);
                //error(previous(), "Unexpected token");
                
        }

        // throw error(peek(), "Expect expression.");
    }

    private boolean match (TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        OurPL.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while(!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch(peek().type) {
                case STRUCT:
                case FOR:
                case FUN:
                case IF:
                case PRINT:
                case RETURN:
                case VAR:
                case WHILE:
                    return;
            }
            advance();
        }
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
