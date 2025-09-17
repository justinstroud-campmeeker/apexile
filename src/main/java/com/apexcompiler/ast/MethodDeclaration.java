package com.apexcompiler.ast;

import java.util.List;

public class MethodDeclaration extends ASTNode {
    private final String name;
    private final String returnType;
    private final List<Parameter> parameters;
    private final List<String> modifiers;
    private final BlockStatement body;
    private final List<Annotation> annotations;
    
    public MethodDeclaration(String name, String returnType, List<Parameter> parameters, 
                           List<String> modifiers, BlockStatement body, List<Annotation> annotations) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.modifiers = modifiers;
        this.body = body;
        this.annotations = annotations;
    }
    
    public String getName() { return name; }
    public String getReturnType() { return returnType; }
    public List<Parameter> getParameters() { return parameters; }
    public List<String> getModifiers() { return modifiers; }
    public BlockStatement getBody() { return body; }
    public List<Annotation> getAnnotations() { return annotations; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMethodDeclaration(this);
    }
    
    public static class Parameter {
        private final String type;
        private final String name;
        
        public Parameter(String type, String name) {
            this.type = type;
            this.name = name;
        }
        
        public String getType() { return type; }
        public String getName() { return name; }
    }
}