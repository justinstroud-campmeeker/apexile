package com.apexcompiler.ast;

public class ReturnStatement extends Statement {
    private final Expression value;
    
    public ReturnStatement(Expression value) {
        this.value = value;
    }
    
    public Expression getValue() { return value; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }
}