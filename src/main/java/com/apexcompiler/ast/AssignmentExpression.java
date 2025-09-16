package com.apexcompiler.ast;

import com.apexcompiler.lexer.TokenType;

public class AssignmentExpression extends Expression {
    private final Expression target;
    private final TokenType operator;
    private final Expression value;
    
    public AssignmentExpression(Expression target, TokenType operator, Expression value) {
        this.target = target;
        this.operator = operator;
        this.value = value;
    }
    
    public Expression getTarget() { return target; }
    public TokenType getOperator() { return operator; }
    public Expression getValue() { return value; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignmentExpression(this);
    }
}