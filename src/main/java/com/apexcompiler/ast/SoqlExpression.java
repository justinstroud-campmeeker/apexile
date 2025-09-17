package com.apexcompiler.ast;

public class SoqlExpression extends Expression {
    private final String query;
    
    public SoqlExpression(String query) {
        this.query = query;
    }
    
    public String getQuery() { return query; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitSoqlExpression(this);
    }
}