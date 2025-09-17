package com.apexcompiler.parser;

import com.apexcompiler.lexer.Token;
import com.apexcompiler.lexer.TokenType;
import com.apexcompiler.ast.*;
import com.apexcompiler.ast.MethodDeclaration.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApexParser {
    private final List<Token> tokens;
    private int current = 0;
    
    public ApexParser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    public ClassDeclaration parseClass() {
        List<Annotation> annotations = parseAnnotations();
        List<String> modifiers = parseModifiers();
        consume(TokenType.CLASS, "Expected 'class'");
        String className = consume(TokenType.IDENTIFIER, "Expected class name").getLexeme();
        
        String superClass = null;
        if (match(TokenType.EXTENDS)) {
            superClass = consume(TokenType.IDENTIFIER, "Expected superclass name").getLexeme();
        }
        
        List<String> interfaces = new ArrayList<>();
        if (match(TokenType.IMPLEMENTS)) {
            do {
                interfaces.add(consume(TokenType.IDENTIFIER, "Expected interface name").getLexeme());
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.LEFT_BRACE, "Expected '{'");
        
        List<MethodDeclaration> methods = new ArrayList<>();
        List<VariableDeclaration> fields = new ArrayList<>();
        
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            List<Annotation> memberAnnotations = parseAnnotations();
            List<String> memberModifiers = parseModifiers();
            
            if (isFieldDeclaration()) {
                fields.add(parseField(memberModifiers));
            } else {
                methods.add(parseMethod(memberModifiers, memberAnnotations));
            }
        }
        
        consume(TokenType.RIGHT_BRACE, "Expected '}'");
        
        return new ClassDeclaration(className, superClass, interfaces, modifiers, methods, fields, annotations);
    }
    
    private List<Annotation> parseAnnotations() {
        List<Annotation> annotations = new ArrayList<>();
        while (check(TokenType.AT)) {
            advance(); // consume @
            String name = consume(TokenType.IDENTIFIER, "Expected annotation name").getLexeme();
            List<Annotation.AnnotationValue> values = new ArrayList<>();
            
            if (match(TokenType.LEFT_PAREN)) {
                if (!check(TokenType.RIGHT_PAREN)) {
                    do {
                        String paramName = null;
                        Object paramValue;
                        
                        if (check(TokenType.IDENTIFIER) && check(TokenType.ASSIGN, 1)) {
                            paramName = advance().getLexeme();
                            advance(); // consume =
                        }
                        
                        if (check(TokenType.STRING_LITERAL)) {
                            paramValue = advance().getLexeme();
                        } else if (check(TokenType.INTEGER_LITERAL)) {
                            paramValue = Integer.parseInt(advance().getLexeme());
                        } else if (check(TokenType.TRUE) || check(TokenType.FALSE)) {
                            paramValue = Boolean.parseBoolean(advance().getLexeme());
                        } else {
                            paramValue = advance().getLexeme();
                        }
                        
                        values.add(new Annotation.AnnotationValue(paramName, paramValue));
                    } while (match(TokenType.COMMA));
                }
                consume(TokenType.RIGHT_PAREN, "Expected ')'");
            }
            
            annotations.add(new Annotation(name, values));
        }
        return annotations;
    }
    
    private List<String> parseModifiers() {
        List<String> modifiers = new ArrayList<>();
        while (isModifier(peek().getType())) {
            modifiers.add(advance().getLexeme());
        }
        return modifiers;
    }
    
    private boolean isModifier(TokenType type) {
        return type == TokenType.PUBLIC || type == TokenType.PRIVATE || 
               type == TokenType.PROTECTED || type == TokenType.STATIC ||
               type == TokenType.FINAL || type == TokenType.ABSTRACT ||
               type == TokenType.VIRTUAL || type == TokenType.OVERRIDE;
    }
    
    private boolean isType(Token token) {
        return token.getType() == TokenType.INTEGER ||
               token.getType() == TokenType.DECIMAL ||
               token.getType() == TokenType.STRING ||
               token.getType() == TokenType.BOOLEAN ||
               token.getType() == TokenType.VOID ||
               token.getType() == TokenType.LIST ||
               token.getType() == TokenType.SET ||
               token.getType() == TokenType.MAP ||
               token.getType() == TokenType.ID ||
               token.getType() == TokenType.DATE ||
               token.getType() == TokenType.DATETIME ||
               token.getType() == TokenType.TIME ||
               token.getType() == TokenType.SOBJECT ||
               (token.getType() == TokenType.IDENTIFIER && !isKeyword(token));
    }
    
    private boolean isKeyword(Token token) {
        switch (token.getType()) {
            case IF:
            case ELSE:
            case FOR:
            case WHILE:
            case RETURN:
            case BREAK:
            case CONTINUE:
            case TRY:
            case CATCH:
            case FINALLY:
            case THROW:
            case NEW:
            case THIS:
            case SUPER:
            case NULL:
            case TRUE:
            case FALSE:
                return true;
            default:
                return false;
        }
    }
    
    private GenericType parseGenericType() {
        String baseType = advance().getLexeme();
        List<GenericType> typeArgs = new ArrayList<>();
        
        if (match(TokenType.LESS_THAN)) {
            do {
                typeArgs.add(parseGenericType());
            } while (match(TokenType.COMMA));
            consume(TokenType.GREATER_THAN, "Expected '>'");
        }
        
        return new GenericType(baseType, typeArgs);
    }
    
    private boolean isFieldDeclaration() {
        int savedCurrent = current;
        
        if (isType(peek())) {
            advance();
            if (check(TokenType.LESS_THAN)) {
                while (!check(TokenType.GREATER_THAN) && !isAtEnd()) {
                    advance();
                }
                if (check(TokenType.GREATER_THAN)) {
                    advance();
                }
            }
            if (check(TokenType.IDENTIFIER)) {
                advance();
                if (check(TokenType.SEMICOLON) || check(TokenType.ASSIGN)) {
                    current = savedCurrent;
                    return true;
                }
            }
        }
        
        current = savedCurrent;
        return false;
    }
    
    private VariableDeclaration parseField(List<String> modifiers) {
        GenericType type = parseGenericType();
        String name = consume(TokenType.IDENTIFIER, "Expected field name").getLexeme();
        
        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new VariableDeclaration(type, name, initializer, modifiers);
    }
    
    private MethodDeclaration parseMethod(List<String> modifiers, List<Annotation> annotations) {
        String returnType = "void";
        String methodName;
        
        if (check(TokenType.IDENTIFIER) && check(TokenType.LEFT_PAREN, 1)) {
            methodName = advance().getLexeme();
        } else {
            returnType = advance().getLexeme();
            methodName = consume(TokenType.IDENTIFIER, "Expected method name").getLexeme();
        }
        
        consume(TokenType.LEFT_PAREN, "Expected '('");
        List<Parameter> parameters = new ArrayList<>();
        
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                String paramType = advance().getLexeme();
                String paramName = consume(TokenType.IDENTIFIER, "Expected parameter name").getLexeme();
                parameters.add(new Parameter(paramType, paramName));
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RIGHT_PAREN, "Expected ')'");
        
        BlockStatement body = parseBlockStatement();
        
        return new MethodDeclaration(methodName, returnType, parameters, modifiers, body, annotations);
    }
    
    private BlockStatement parseBlockStatement() {
        consume(TokenType.LEFT_BRACE, "Expected '{'");
        
        List<Statement> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }
        
        consume(TokenType.RIGHT_BRACE, "Expected '}'");
        return new BlockStatement(statements);
    }
    
    private Statement parseStatement() {
        if (match(TokenType.IF)) return parseIfStatement();
        if (match(TokenType.WHILE)) return parseWhileStatement();
        if (match(TokenType.FOR)) return parseForStatement();
        if (match(TokenType.RETURN)) return parseReturnStatement();
        if (match(TokenType.LEFT_BRACE)) return parseBlockStatement();
        if (match(TokenType.INSERT, TokenType.UPDATE, TokenType.DELETE, TokenType.UPSERT)) return parseDmlStatement();
        
        if (isType(peek()) && check(TokenType.IDENTIFIER, 1)) {
            return parseVariableDeclaration();
        }
        
        return parseExpressionStatement();
    }
    
    private Statement parseDmlStatement() {
        TokenType operation = previous().getType();
        Expression target = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new DmlStatement(operation, target);
    }
    
    private Statement parseIfStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '('");
        Expression condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expected ')'");
        
        Statement thenBranch = parseBlockStatement();
        Statement elseBranch = null;
        
        if (match(TokenType.ELSE)) {
            elseBranch = parseBlockStatement();
        }
        
        return new IfStatement(condition, thenBranch, elseBranch);
    }
    
    private Statement parseWhileStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '('");
        Expression condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expected ')'");
        Statement body = parseStatement();
        
        return new WhileStatement(condition, body);
    }
    
    private Statement parseForStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '('");
        
        Statement initializer = null;
        if (!check(TokenType.SEMICOLON)) {
            initializer = parseStatement();
        } else {
            advance();
        }
        
        Expression condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expected ';'");
        
        Expression increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = parseExpression();
        }
        consume(TokenType.RIGHT_PAREN, "Expected ')'");
        
        Statement body = parseStatement();
        
        return new ForStatement(initializer, condition, increment, body);
    }
    
    private Statement parseReturnStatement() {
        Expression value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new ReturnStatement(value);
    }
    
    private Statement parseVariableDeclaration() {
        String type = advance().getLexeme();
        String name = consume(TokenType.IDENTIFIER, "Expected variable name").getLexeme();
        
        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new VariableDeclaration(type, name, initializer, new ArrayList<>());
    }
    
    private Statement parseExpressionStatement() {
        Expression expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new ExpressionStatement(expr);
    }
    
    private Expression parseExpression() {
        return parseAssignment();
    }
    
    private Expression parseAssignment() {
        Expression expr = parseLogicalOr();
        
        if (match(TokenType.ASSIGN, TokenType.PLUS_ASSIGN, TokenType.MINUS_ASSIGN,
                 TokenType.MULTIPLY_ASSIGN, TokenType.DIVIDE_ASSIGN)) {
            TokenType operator = previous().getType();
            Expression value = parseAssignment();
            return new AssignmentExpression(expr, operator, value);
        }
        
        return expr;
    }
    
    private Expression parseLogicalOr() {
        Expression expr = parseLogicalAnd();
        
        while (match(TokenType.LOGICAL_OR)) {
            TokenType operator = previous().getType();
            Expression right = parseLogicalAnd();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression parseLogicalAnd() {
        Expression expr = parseEquality();
        
        while (match(TokenType.LOGICAL_AND)) {
            TokenType operator = previous().getType();
            Expression right = parseEquality();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression parseEquality() {
        Expression expr = parseComparison();
        
        while (match(TokenType.EQUALS, TokenType.NOT_EQUALS)) {
            TokenType operator = previous().getType();
            Expression right = parseComparison();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression parseComparison() {
        Expression expr = parseTerm();
        
        while (match(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL,
                    TokenType.LESS_THAN, TokenType.LESS_EQUAL)) {
            TokenType operator = previous().getType();
            Expression right = parseTerm();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression parseTerm() {
        Expression expr = parseFactor();
        
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType operator = previous().getType();
            Expression right = parseFactor();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression parseFactor() {
        Expression expr = parseUnary();
        
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            TokenType operator = previous().getType();
            Expression right = parseUnary();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression parseUnary() {
        if (match(TokenType.LOGICAL_NOT, TokenType.MINUS, TokenType.PLUS)) {
            TokenType operator = previous().getType();
            Expression right = parseUnary();
            return new UnaryExpression(operator, right);
        }
        
        return parseCall();
    }
    
    private Expression parseCall() {
        Expression expr = parsePrimary();
        
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(TokenType.DOT)) {
                String name = consume(TokenType.IDENTIFIER, "Expected property name").getLexeme();
                expr = new MemberExpression(expr, name);
            } else {
                break;
            }
        }
        
        return expr;
    }
    
    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RIGHT_PAREN, "Expected ')'");
        return new CallExpression(callee, arguments);
    }
    
    private Expression parsePrimary() {
        if (match(TokenType.TRUE)) {
            return new LiteralExpression(true, "Boolean");
        }
        
        if (match(TokenType.FALSE)) {
            return new LiteralExpression(false, "Boolean");
        }
        
        if (match(TokenType.NULL)) {
            return new LiteralExpression(null, "null");
        }
        
        if (match(TokenType.INTEGER_LITERAL)) {
            return new LiteralExpression(Integer.parseInt(previous().getLexeme()), "Integer");
        }
        
        if (match(TokenType.DECIMAL_LITERAL)) {
            return new LiteralExpression(Double.parseDouble(previous().getLexeme()), "Decimal");
        }
        
        if (match(TokenType.STRING_LITERAL)) {
            return new LiteralExpression(previous().getLexeme(), "String");
        }
        
        if (match(TokenType.SOQL_LITERAL)) {
            return new SoqlExpression(previous().getLexeme());
        }
        
        if (match(TokenType.IDENTIFIER)) {
            return new IdentifierExpression(previous().getLexeme());
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            Expression expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expected ')'");
            return expr;
        }
        
        throw new RuntimeException("Unexpected token: " + peek().getLexeme());
    }
    
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    private boolean check(TokenType type, int offset) {
        if (current + offset >= tokens.size()) return false;
        return tokens.get(current + offset).getType() == type;
    }
    
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token peekNext() {
        if (current + 1 >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(current + 1);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException(message + " at line " + peek().getLine());
    }
}