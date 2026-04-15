package cpsc326;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


public class ParserTest {
    private List<Token> scan(String source) {
        OurPL.hadError = false;
        return new Lexer(source).scanTokens();
    }

    private void assertToken(Token token, TokenType type, String lexeme, Object literal, int line) {
        assertEquals(type, token.type);
        assertEquals(lexeme, token.lexeme);
        assertEquals(literal, token.literal);
        assertEquals(line, token.line);
    }

    @Test
    void assertnilPrintsNil(){
        List<Token> tokens = scan("nil");
        Parser parser = new Parser(tokens);
        Expr expr = parser.parse();
        ASTPrinter interpreter = new ASTPrinter();
        assertEquals("nil", interpreter.print(expr));
    }
}
