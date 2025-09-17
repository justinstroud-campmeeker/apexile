package com.apexcompiler.ast;

import java.util.List;

public class VariableDeclaration extends Statement {
    private final GenericType type;
    private final String name;
    private final Expression initializer;
    private final List<String> modifiers;
    
    public VariableDeclaration(GenericType type, String name, Expression initializer, List<String> modifiers) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.modifiers = modifiers;
    }
    
    // Convenience constructor for non-generic types
    public VariableDeclaration(String type, String name, Expression initializer, List<String> modifiers) {
        this(new GenericType(type), name, initializer, modifiers);
    }
    
    public GenericType getType() { return type; }
    public String getName() { return name; }
    public Expression getInitializer() { return initializer; }
    public List<String> getModifiers() { return modifiers; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariableDeclaration(this);
    }
}