package com.apexcompiler.lexer;

import java.util.*;

public class ApexLexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;
    
    private static final Map<String, TokenType> keywords = new HashMap<>();
    
    static {
        keywords.put("class", TokenType.CLASS);
        keywords.put("public", TokenType.PUBLIC);
        keywords.put("private", TokenType.PRIVATE);
        keywords.put("protected", TokenType.PROTECTED);
        keywords.put("static", TokenType.STATIC);
        keywords.put("final", TokenType.FINAL);
        keywords.put("abstract", TokenType.ABSTRACT);
        keywords.put("virtual", TokenType.VIRTUAL);
        keywords.put("override", TokenType.OVERRIDE);
        keywords.put("interface", TokenType.INTERFACE);
        keywords.put("extends", TokenType.EXTENDS);
        keywords.put("implements", TokenType.IMPLEMENTS);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("do", TokenType.DO);
        keywords.put("return", TokenType.RETURN);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("try", TokenType.TRY);
        keywords.put("catch", TokenType.CATCH);
        keywords.put("finally", TokenType.FINALLY);
        keywords.put("throw", TokenType.THROW);
        keywords.put("new", TokenType.NEW);
        keywords.put("this", TokenType.THIS);
        keywords.put("super", TokenType.SUPER);
        keywords.put("null", TokenType.NULL);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("void", TokenType.VOID);
        keywords.put("Integer", TokenType.INTEGER);
        keywords.put("Decimal", TokenType.DECIMAL);
        keywords.put("String", TokenType.STRING);
        keywords.put("Boolean", TokenType.BOOLEAN);
        keywords.put("Id", TokenType.ID);
        keywords.put("Date", TokenType.DATE);
        keywords.put("DateTime", TokenType.DATETIME);
        keywords.put("Time", TokenType.TIME);
        keywords.put("SObject", TokenType.SOBJECT);
        keywords.put("List", TokenType.LIST);
        keywords.put("Set", TokenType.SET);
        keywords.put("Map", TokenType.MAP);
        keywords.put("insert", TokenType.INSERT);
        keywords.put("update", TokenType.UPDATE);
        keywords.put("delete", TokenType.DELETE);
        keywords.put("upsert", TokenType.UPSERT);
    }
    
    public ApexLexer(String source) {
        this.source = source;
    }
    
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
    
    private void scanToken() {
        char c = advance();
        
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                column = 0;
                break;
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '[':
                if (isSoqlQuery()) {
                    soqlQuery();
                } else {
                    addToken(TokenType.LEFT_BRACKET);
                }
                break;
            case ']':
                addToken(TokenType.RIGHT_BRACKET);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '?':
                addToken(TokenType.QUESTION);
                break;
            case ':':
                addToken(TokenType.COLON);
                break;
            case '@':
                addToken(TokenType.AT);
                break;
            case '+':
                if (match('+')) {
                    addToken(TokenType.INCREMENT);
                } else if (match('=')) {
                    addToken(TokenType.PLUS_ASSIGN);
                } else {
                    addToken(TokenType.PLUS);
                }
                break;
            case '-':
                if (match('-')) {
                    addToken(TokenType.DECREMENT);
                } else if (match('=')) {
                    addToken(TokenType.MINUS_ASSIGN);
                } else {
                    addToken(TokenType.MINUS);
                }
                break;
            case '*':
                addToken(match('=') ? TokenType.MULTIPLY_ASSIGN : TokenType.MULTIPLY);
                break;
            case '%':
                addToken(TokenType.MODULO);
                break;
            case '!':
                addToken(match('=') ? TokenType.NOT_EQUALS : TokenType.LOGICAL_NOT);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUALS : TokenType.ASSIGN);
                break;
            case '<':
                if (match('<')) {
                    addToken(TokenType.LEFT_SHIFT);
                } else if (match('=')) {
                    addToken(TokenType.LESS_EQUAL);
                } else {
                    addToken(TokenType.LESS_THAN);
                }
                break;
            case '>':
                if (match('>')) {
                    addToken(TokenType.RIGHT_SHIFT);
                } else if (match('=')) {
                    addToken(TokenType.GREATER_EQUAL);
                } else {
                    addToken(TokenType.GREATER_THAN);
                }
                break;
            case '&':
                addToken(match('&') ? TokenType.LOGICAL_AND : TokenType.BITWISE_AND);
                break;
            case '|':
                addToken(match('|') ? TokenType.LOGICAL_OR : TokenType.BITWISE_OR);
                break;
            case '^':
                addToken(TokenType.BITWISE_XOR);
                break;
            case '~':
                addToken(TokenType.BITWISE_NOT);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    blockComment();
                } else if (match('=')) {
                    addToken(TokenType.DIVIDE_ASSIGN);
                } else {
                    addToken(TokenType.DIVIDE);
                }
                break;
            case '\'':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                }
                break;
        }
    }
    
    private void blockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance();
                advance();
                break;
            }
            if (peek() == '\n') {
                line++;
                column = 0;
            }
            advance();
        }
    }
    
    private void string() {
        while (peek() != '\'' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 0;
            }
            if (peek() == '\\') {
                advance();
                if (!isAtEnd()) advance();
            } else {
                advance();
            }
        }
        
        if (isAtEnd()) {
            throw new RuntimeException("Unterminated string at line " + line);
        }
        
        advance();
        
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING_LITERAL, value);
    }
    
    private void number() {
        while (isDigit(peek())) advance();
        
        boolean isDecimal = false;
        if (peek() == '.' && isDigit(peekNext())) {
            isDecimal = true;
            advance();
            while (isDigit(peek())) advance();
        }
        
        String value = source.substring(start, current);
        addToken(isDecimal ? TokenType.DECIMAL_LITERAL : TokenType.INTEGER_LITERAL, value);
    }
    
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        
        current++;
        column++;
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
    
    private char advance() {
        column++;
        return source.charAt(current++);
    }
    
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    
    private void addToken(TokenType type, String literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, literal != null ? literal : text, line, column - text.length()));
    }
    
    private boolean isSoqlQuery() {
        int saved = current;
        advance(); // consume [
        
        while (!isAtEnd() && peek() != ']') {
            if (peek() == '\n') {
                line++;
                column = 0;
            }
            advance();
        }
        
        boolean isSoql = !isAtEnd();
        current = saved;
        return isSoql;
    }
    
    private void soqlQuery() {
        advance(); // consume [
        StringBuilder query = new StringBuilder();
        
        while (peek() != ']' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 0;
            }
            query.append(advance());
        }
        
        if (isAtEnd()) {
            throw new RuntimeException("Unterminated SOQL query at line " + line);
        }
        
        advance(); // consume ]
        
        String soqlQuery = query.toString().trim();
        addToken(TokenType.SOQL_LITERAL, soqlQuery);
    }
    
    private boolean isAtEnd() {
        return current >= source.length();
    }
}