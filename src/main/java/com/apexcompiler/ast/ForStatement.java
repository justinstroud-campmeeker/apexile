package com.apexcompiler.ast;

public class ForStatement extends Statement {
    private final Statement initializer;
    private final Expression condition;
    private final Expression increment;
    private final Statement body;
    
    public ForStatement(Statement initializer, Expression condition, Expression increment, Statement body) {
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }
    
    public Statement getInitializer() { return initializer; }
    public Expression getCondition() { return condition; }
    public Expression getIncrement() { return increment; }
    public Statement getBody() { return body; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitForStatement(this);
    }
}