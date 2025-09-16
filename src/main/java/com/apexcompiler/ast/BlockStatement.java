package com.apexcompiler.ast;

import java.util.List;

public class BlockStatement extends Statement {
    private final List<Statement> statements;
    
    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }
    
    public List<Statement> getStatements() {
        return statements;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBlockStatement(this);
    }
}