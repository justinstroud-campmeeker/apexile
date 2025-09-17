package com.apexcompiler.ast;

import java.util.List;

public class ClassDeclaration extends ASTNode {
    private final String name;
    private final String superClass;
    private final List<String> interfaces;
    private final List<String> modifiers;
    private final List<MethodDeclaration> methods;
    private final List<VariableDeclaration> fields;
    private final List<Annotation> annotations;
    
    public ClassDeclaration(String name, String superClass, List<String> interfaces, 
                          List<String> modifiers, List<MethodDeclaration> methods, 
                          List<VariableDeclaration> fields, List<Annotation> annotations) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.modifiers = modifiers;
        this.methods = methods;
        this.fields = fields;
        this.annotations = annotations;
    }
    
    public String getName() { return name; }
    public String getSuperClass() { return superClass; }
    public List<String> getInterfaces() { return interfaces; }
    public List<String> getModifiers() { return modifiers; }
    public List<MethodDeclaration> getMethods() { return methods; }
    public List<VariableDeclaration> getFields() { return fields; }
    public List<Annotation> getAnnotations() { return annotations; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitClassDeclaration(this);
    }
}