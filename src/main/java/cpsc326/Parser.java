package cpsc326;

import java.util.List;

import static cpsc326.TokenType.BANG;
import static cpsc326.TokenType.BANG_EQUAL;
import static cpsc326.TokenType.EOF;
import static cpsc326.TokenType.EQUAL_EQUAL;
import static cpsc326.TokenType.FOR;
import static cpsc326.TokenType.FUN;
import static cpsc326.TokenType.GREATER;
import static cpsc326.TokenType.GREATER_EQUAL;
import static cpsc326.TokenType.IF;
import static cpsc326.TokenType.LEFT_PAREN;
import static cpsc326.TokenType.LESS;
import static cpsc326.TokenType.LESS_EQUAL;
import static cpsc326.TokenType.MINUS;
import static cpsc326.TokenType.NUMBER;
import static cpsc326.TokenType.PLUS;
import static cpsc326.TokenType.PRINT;
import static cpsc326.TokenType.RETURN;
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

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        // TODO complete function
        return equality();

    }

    private Expr equality() {
        // TODO complete function
        
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
        // TODO complete function
        
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
        // TODO complete function

        // Another binary opersaator should be the same
        Expr left = factor();

        while(match(PLUS, MINUS)){
            Token termToken = previous();
            Expr right = factor();
            System.out.println("Creating a term of type:" + termToken.lexeme);
            left = new Expr.Binary(left, termToken, right);
        }

        //ASTPrinter printer = new ASTPrinter();
        //String output = printer.print(left);
        //System.out.println(output);
        return left;
    }

    private Expr factor() {
        // TODO complete function

        Expr left = unary();
        while(match(STAR, SLASH)){
            Token factorToken = previous();
            Expr right = unary();
            left = new Expr.Binary(left, factorToken, right);
        }

        return left;
        
    }

    private Expr unary() {
        // TODO complete function

        
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
                Expr expr = expression();
                consume(RIGHT_PAREN, "Right parentheses expected");
                return expr;
            default:
                // Synchronize?
                synchronize();
                return expression();
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
        //OurPL.error(token, message);
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
