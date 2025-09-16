package com.apexcompiler.ast;

import java.util.List;

public class VariableDeclaration extends Statement {
    private final String type;
    private final String name;
    private final Expression initializer;
    private final List<String> modifiers;
    
    public VariableDeclaration(String type, String name, Expression initializer, List<String> modifiers) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.modifiers = modifiers;
    }
    
    public String getType() { return type; }
    public String getName() { return name; }
    public Expression getInitializer() { return initializer; }
    public List<String> getModifiers() { return modifiers; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariableDeclaration(this);
    }
}