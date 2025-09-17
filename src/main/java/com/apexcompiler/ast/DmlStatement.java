package com.apexcompiler.ast;

import com.apexcompiler.lexer.TokenType;

public class DmlStatement extends Statement {
    private final TokenType operation;
    private final Expression target;
    
    public DmlStatement(TokenType operation, Expression target) {
        this.operation = operation;
        this.target = target;
    }
    
    public TokenType getOperation() { return operation; }
    public Expression getTarget() { return target; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDmlStatement(this);
    }
}