package cpsc326;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cpsc326.OurPL.error;
import static cpsc326.TokenType.AND;
import static cpsc326.TokenType.BANG;
import static cpsc326.TokenType.BANG_EQUAL;
import static cpsc326.TokenType.COMMA;
import static cpsc326.TokenType.DOT;
import static cpsc326.TokenType.ELSE;
import static cpsc326.TokenType.EOF;
import static cpsc326.TokenType.EQUAL;
import static cpsc326.TokenType.EQUAL_EQUAL;
import static cpsc326.TokenType.FALSE;
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
import static cpsc326.TokenType.NIL;
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
import static cpsc326.TokenType.THIS;
import static cpsc326.TokenType.TRUE;
import static cpsc326.TokenType.VAR;
import static cpsc326.TokenType.WHILE;

class Lexer {
    private final String source;
    public final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;
    
    Lexer(String source) {
        this.source = source;
    }

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("else", ELSE);
        keywords.put("struct", STRUCT);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("while", WHILE);
        
        
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

     private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isAlphaNumeric(char c) {
        return c == '_' || isAlpha(c) || isDigit(c);
    }

    private void addToken(TokenType type) {
        addToken(type,null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void string() {
        // TODO: implement string()

        String literal = "";
        int new_start = current;
        advance();
        
        while(peek() != '\"' && !isAtEnd()){
            if(peek() == '\n'){
                ++line;
                
                //Could cause problems, not sure if we want a newline character to be part of a string?
            }
            
            
            advance();
        }
        if(isAtEnd()){
            error(line, "Unterminated string.");
            return;
        }
        literal = source.substring(new_start, current);
        System.out.println("Literal : " + literal);
        advance();
        addToken(STRING, literal);
        
        return;
    }

    private void number() {
        // TODO: implement number()
        
        while(isDigit(peek())){
            advance();
        }
        if(peek() == '.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek())){
                advance();
            }
        }
        String numberInput = source.substring(start, current);
        Double actualNumber = Double.parseDouble(source.substring(start, current));
        
        addToken(NUMBER, actualNumber);
    }

    private void identifier() {
        // TODO: implement identifier()
        while(isAlphaNumeric(peek())){
            advance();
        }
        String check = source.substring(start, current);
        if(keywords.containsKey(check)){
            addToken(keywords.get(check));
        }else{
            addToken(IDENTIFIER);
        }
    }

    private void scanToken() {
        // TODO: implement scanToken()
        
        char check = advance();
        
        //System.out.println("current : " + current + ", peek : " + peek() + ", peekNext : " + peekNext());
        switch (check) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '/':
                addToken(SLASH);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '!':
                if (match('=')){
                    addToken(BANG_EQUAL);
                }else{
                    addToken(BANG);
                }
                break;
            case '=':
                if(match('=')){
                    addToken(EQUAL_EQUAL);
                }else{
                    addToken(EQUAL);
                }
                break;
            case '<':
                if(match('=')){
                    addToken(LESS_EQUAL);
                }else{
                    addToken(LESS);
                }
                break;
            case '>':
                if(match('=')){
                    addToken(GREATER_EQUAL);
                }else{
                    addToken(GREATER);
                }
                break;
            case '\n':
                ++line;
                break;
            case ' ':
                break;
            case '\t':
                break;
            case '\r':
                break;
            case '#':
                while(peek() != '\n' && !isAtEnd()){
                    advance();
                } 
                if(peek() == '\n' && !isAtEnd()){
                    advance();
                    ++line;
                }
                System.out.println("peek: " + peek());
                break;
            case '\"':
                string();
                break;
            default:
                if(isAlphaNumeric(check)){
                    if(isDigit(check)){
                        // Call Number
                        number();
                    }
                    else if(isAlpha(check) && check != '_'){
                        identifier();
                    }else{
                        
                        error(line, "Unexpected character.");
                    }
                }else{
                    error(line, "Unexpected character.");
                }
                break;
        }
        // No cases match so should be an identifier or a digit
        // May want to wrap these if statements in the isAlphaNumeric and isAlpha
    }
}
