package com.apexcompiler.ast;

import com.apexcompiler.lexer.TokenType;

public class UnaryExpression extends Expression {
    private final TokenType operator;
    private final Expression operand;
    
    public UnaryExpression(TokenType operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }
    
    public TokenType getOperator() { return operator; }
    public Expression getOperand() { return operand; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUnaryExpression(this);
    }
}