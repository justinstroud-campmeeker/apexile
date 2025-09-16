package com.apexcompiler.ast;

public class IdentifierExpression extends Expression {
    private final String name;
    
    public IdentifierExpression(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIdentifierExpression(this);
    }
}