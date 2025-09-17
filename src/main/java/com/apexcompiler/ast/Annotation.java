package com.apexcompiler.ast;

import java.util.List;

public class Annotation extends ASTNode {
    private final String name;
    private final List<AnnotationValue> values;
    
    public Annotation(String name, List<AnnotationValue> values) {
        this.name = name;
        this.values = values;
    }
    
    public String getName() { return name; }
    public List<AnnotationValue> getValues() { return values; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAnnotation(this);
    }
    
    public static class AnnotationValue {
        private final String name;
        private final Object value;
        
        public AnnotationValue(String name, Object value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() { return name; }
        public Object getValue() { return value; }
    }
}