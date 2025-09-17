package com.apexcompiler.ast;

import java.util.List;

public class GenericType {
    private final String baseType;
    private final List<GenericType> typeArguments;
    
    public GenericType(String baseType, List<GenericType> typeArguments) {
        this.baseType = baseType;
        this.typeArguments = typeArguments;
    }
    
    public GenericType(String baseType) {
        this(baseType, List.of());
    }
    
    public String getBaseType() { return baseType; }
    public List<GenericType> getTypeArguments() { return typeArguments; }
    
    public boolean isGeneric() {
        return !typeArguments.isEmpty();
    }
    
    @Override
    public String toString() {
        if (typeArguments.isEmpty()) {
            return baseType;
        }
        
        StringBuilder sb = new StringBuilder(baseType);
        sb.append("<");
        for (int i = 0; i < typeArguments.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(typeArguments.get(i).toString());
        }
        sb.append(">");
        return sb.toString();
    }
    
    public String toJavaType() {
        if (typeArguments.isEmpty()) {
            return apexToJavaType(baseType);
        }
        
        StringBuilder sb = new StringBuilder(apexToJavaType(baseType));
        sb.append("<");
        for (int i = 0; i < typeArguments.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(typeArguments.get(i).toJavaType());
        }
        sb.append(">");
        return sb.toString();
    }
    
    private String apexToJavaType(String apexType) {
        switch (apexType) {
            case "Integer": return "Integer";
            case "Decimal": return "Double";
            case "String": return "String";
            case "Boolean": return "Boolean";
            case "List": return "java.util.List";
            case "Set": return "java.util.Set";
            case "Map": return "java.util.Map";
            case "Id": return "String";
            default: return apexType;
        }
    }
}