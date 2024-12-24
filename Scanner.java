import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("func",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("display",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("parent",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("let",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }


    // Generate List of tokens from source String
    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch(c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': 
                addToken(match('*') ? TokenType.POWER : TokenType.STAR);
                break;
            case '^':
                addToken(TokenType.BITWISE_XOR); break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case'>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '&':
                addToken(match('&') ? TokenType.AND : TokenType.BITWISE_AND);
                break;
            case '|':
                addToken(match('|') ? TokenType.OR : TokenType.BITWISE_OR);
                break;
            case '/':
                if(match('/')) {
                    // '//' is a comment in Krystal Script.
                    // Skip the line is // is present
                    while(peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            
            // Skip spaces and other invisible characters
            case ' ':
            case '\r':
            case '\t':
                break;
            // Go the next line if you encounter \n
            case '\n':
                line++;
                break;
            // Reading String literals
            case '"':
                string();
                break;
            default: 
                if(isDigit(c)) {
                    number();
                } else if(isAlpha(c)) {
                    identifier();
                }else {
                    KrystalScript.error(line, "Unexpected character.");
                }
                break;
                
        }
    }


    // Has reading the source completed?
    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    // Handle lexems that may have 2 characters like != <=, etc
    private boolean match(char expected) {
        if(isAtEnd()) return false; // False if source reading is over
        if(source.charAt(current) != expected) return false; // False if expected character is not what we got
        // Otherwise the character has matched
        current++;
        return true;
    }

    // get a peek of the current character
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if(current+1 > source.length()) return '\0';
        else return source.charAt(current+1);
    }

    // Reading string literal lexemes
    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }

        if(isAtEnd()) {
            KrystalScript.error(line, "Unterminated String");
            return;
        }

        advance(); // To get the closing "

        // Get the string literal
        String value = source.substring(start + 1, current-1);
        addToken(TokenType.STRING, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Get the number
    private void number() {
        while(isDigit(peek())) advance();

        // Check for a decimal .
        if(peek() == '.' && isDigit(peekNext())) {
            advance();

            while(isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while(isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);

        if(type==null) type = TokenType.IDENTIFIER;
        
        addToken(type);
    }
}
