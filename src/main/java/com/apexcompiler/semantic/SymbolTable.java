package com.apexcompiler.semantic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable parent;
    
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }
    
    public void define(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }
    
    public Symbol lookup(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        
        if (parent != null) {
            return parent.lookup(name);
        }
        
        return null;
    }
    
    public boolean isDefined(String name) {
        return symbols.containsKey(name);
    }
    
    public SymbolTable getParent() {
        return parent;
    }
    
    public static class Symbol {
        private final String name;
        private final String type;
        private final SymbolKind kind;
        
        public Symbol(String name, String type, SymbolKind kind) {
            this.name = name;
            this.type = type;
            this.kind = kind;
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public SymbolKind getKind() { return kind; }
    }
    
    public enum SymbolKind {
        VARIABLE,
        METHOD,
        CLASS,
        PARAMETER
    }
}