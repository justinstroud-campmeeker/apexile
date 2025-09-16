package com.apexcompiler.ast;

import java.util.List;

public class CallExpression extends Expression {
    private final Expression callee;
    private final List<Expression> arguments;
    
    public CallExpression(Expression callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }
    
    public Expression getCallee() { return callee; }
    public List<Expression> getArguments() { return arguments; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCallExpression(this);
    }
}