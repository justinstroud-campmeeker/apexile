package com.apexcompiler.ast;

public class LiteralExpression extends Expression {
    private final Object value;
    private final String type;
    
    public LiteralExpression(Object value, String type) {
        this.value = value;
        this.type = type;
    }
    
    public Object getValue() { return value; }
    public String getType() { return type; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteralExpression(this);
    }
}