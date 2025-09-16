package com.apexcompiler.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ApexLexerTest {
    
    @Test
    public void testBasicTokens() {
        String source = "public class Test { }";
        ApexLexer lexer = new ApexLexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.PUBLIC, tokens.get(0).getType());
        assertEquals(TokenType.CLASS, tokens.get(1).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(2).getType());
        assertEquals("Test", tokens.get(2).getLexeme());
        assertEquals(TokenType.LEFT_BRACE, tokens.get(3).getType());
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(4).getType());
        assertEquals(TokenType.EOF, tokens.get(5).getType());
    }
    
    @Test
    public void testStringLiterals() {
        String source = "'Hello World'";
        ApexLexer lexer = new ApexLexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.STRING_LITERAL, tokens.get(0).getType());
        assertEquals("Hello World", tokens.get(0).getLexeme());
    }
    
    @Test
    public void testNumbers() {
        String source = "42 3.14";
        ApexLexer lexer = new ApexLexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.INTEGER_LITERAL, tokens.get(0).getType());
        assertEquals("42", tokens.get(0).getLexeme());
        assertEquals(TokenType.DECIMAL_LITERAL, tokens.get(1).getType());
        assertEquals("3.14", tokens.get(1).getLexeme());
    }
    
    @Test
    public void testOperators() {
        String source = "+ - * / == != < > <= >= && ||";
        ApexLexer lexer = new ApexLexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.PLUS, tokens.get(0).getType());
        assertEquals(TokenType.MINUS, tokens.get(1).getType());
        assertEquals(TokenType.MULTIPLY, tokens.get(2).getType());
        assertEquals(TokenType.DIVIDE, tokens.get(3).getType());
        assertEquals(TokenType.EQUALS, tokens.get(4).getType());
        assertEquals(TokenType.NOT_EQUALS, tokens.get(5).getType());
        assertEquals(TokenType.LESS_THAN, tokens.get(6).getType());
        assertEquals(TokenType.GREATER_THAN, tokens.get(7).getType());
        assertEquals(TokenType.LESS_EQUAL, tokens.get(8).getType());
        assertEquals(TokenType.GREATER_EQUAL, tokens.get(9).getType());
        assertEquals(TokenType.LOGICAL_AND, tokens.get(10).getType());
        assertEquals(TokenType.LOGICAL_OR, tokens.get(11).getType());
    }
    
    @Test
    public void testComments() {
        String source = "// This is a comment\n/* Block comment */\nclass Test {}";
        ApexLexer lexer = new ApexLexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.CLASS, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("Test", tokens.get(1).getLexeme());
    }
}