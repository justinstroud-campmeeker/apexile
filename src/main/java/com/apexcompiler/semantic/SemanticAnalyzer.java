package com.apexcompiler.semantic;

import com.apexcompiler.ast.*;
import com.apexcompiler.semantic.SymbolTable.Symbol;
import com.apexcompiler.semantic.SymbolTable.SymbolKind;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer implements ASTVisitor<String> {
    private SymbolTable currentScope;
    private final List<String> errors = new ArrayList<>();
    private String currentClass;
    private String currentMethod;
    
    public SemanticAnalyzer() {
        this.currentScope = new SymbolTable(null);
        initializeBuiltinTypes();
    }
    
    private void initializeBuiltinTypes() {
        // Primitive types
        currentScope.define(new Symbol("Integer", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Decimal", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("String", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Boolean", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Date", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("DateTime", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Time", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Id", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Blob", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Object", "Type", SymbolKind.CLASS));
        
        // Collection types
        currentScope.define(new Symbol("List", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Set", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Map", "Type", SymbolKind.CLASS));
        
        // SObject and standard objects
        currentScope.define(new Symbol("SObject", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Account", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Contact", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Lead", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Opportunity", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Case", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("User", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Profile", "Type", SymbolKind.CLASS));
        
        // System classes
        currentScope.define(new Symbol("System", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Database", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Test", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Schema", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Trigger", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Limits", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Math", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Json", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Http", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("HttpRequest", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("HttpResponse", "Type", SymbolKind.CLASS));
        
        // Exception types
        currentScope.define(new Symbol("Exception", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("DmlException", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("QueryException", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("NullPointerException", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("ListException", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("StringException", "Type", SymbolKind.CLASS));
        
        // Apex-specific types
        currentScope.define(new Symbol("PageReference", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("ApexPages", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("Messaging", "Type", SymbolKind.CLASS));
        currentScope.define(new Symbol("SelectOption", "Type", SymbolKind.CLASS));
    }
    
    public List<String> analyze(ClassDeclaration classDecl) {
        errors.clear();
        classDecl.accept(this);
        return errors;
    }
    
    @Override
    public String visitClassDeclaration(ClassDeclaration node) {
        currentClass = node.getName();
        
        if (currentScope.isDefined(node.getName())) {
            errors.add("Class '" + node.getName() + "' is already defined");
        } else {
            currentScope.define(new Symbol(node.getName(), "Class", SymbolKind.CLASS));
        }
        
        SymbolTable classScope = new SymbolTable(currentScope);
        currentScope = classScope;
        
        if (node.getSuperClass() != null) {
            if (currentScope.lookup(node.getSuperClass()) == null) {
                errors.add("Superclass '" + node.getSuperClass() + "' not found");
            }
        }
        
        for (String interfaceName : node.getInterfaces()) {
            if (currentScope.lookup(interfaceName) == null) {
                errors.add("Interface '" + interfaceName + "' not found");
            }
        }
        
        for (VariableDeclaration field : node.getFields()) {
            field.accept(this);
        }
        
        for (MethodDeclaration method : node.getMethods()) {
            method.accept(this);
        }
        
        currentScope = currentScope.getParent();
        currentClass = null;
        return null;
    }
    
    @Override
    public String visitMethodDeclaration(MethodDeclaration node) {
        currentMethod = node.getName();
        
        if (currentScope.isDefined(node.getName())) {
            errors.add("Method '" + node.getName() + "' is already defined in class '" + currentClass + "'");
        } else {
            currentScope.define(new Symbol(node.getName(), node.getReturnType(), SymbolKind.METHOD));
        }
        
        SymbolTable methodScope = new SymbolTable(currentScope);
        currentScope = methodScope;
        
        for (MethodDeclaration.Parameter param : node.getParameters()) {
            if (currentScope.isDefined(param.getName())) {
                errors.add("Parameter '" + param.getName() + "' is already defined");
            } else {
                currentScope.define(new Symbol(param.getName(), param.getType(), SymbolKind.PARAMETER));
            }
            
            if (currentScope.lookup(param.getType()) == null) {
                errors.add("Parameter type '" + param.getType() + "' not found");
            }
        }
        
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        
        currentScope = currentScope.getParent();
        currentMethod = null;
        return node.getReturnType();
    }
    
    @Override
    public String visitVariableDeclaration(VariableDeclaration node) {
        if (currentScope.isDefined(node.getName())) {
            errors.add("Variable '" + node.getName() + "' is already defined");
        } else {
            currentScope.define(new Symbol(node.getName(), node.getType().toString(), SymbolKind.VARIABLE));
        }
        
        if (currentScope.lookup(node.getType().getBaseType()) == null) {
            errors.add("Type '" + node.getType().getBaseType() + "' not found");
        }
        
        if (node.getInitializer() != null) {
            String initType = node.getInitializer().accept(this);
            if (initType != null && !isAssignableFrom(node.getType().toString(), initType)) {
                errors.add("Cannot assign " + initType + " to " + node.getType());
            }
        }
        
        return node.getType().toString();
    }
    
    @Override
    public String visitIfStatement(IfStatement node) {
        String conditionType = node.getCondition().accept(this);
        if (conditionType != null && !conditionType.equals("Boolean")) {
            errors.add("If condition must be Boolean, got " + conditionType);
        }
        
        node.getThenBranch().accept(this);
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        
        return null;
    }
    
    @Override
    public String visitWhileStatement(WhileStatement node) {
        String conditionType = node.getCondition().accept(this);
        if (conditionType != null && !conditionType.equals("Boolean")) {
            errors.add("While condition must be Boolean, got " + conditionType);
        }
        
        node.getBody().accept(this);
        return null;
    }
    
    @Override
    public String visitForStatement(ForStatement node) {
        SymbolTable forScope = new SymbolTable(currentScope);
        currentScope = forScope;
        
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }
        
        if (node.getCondition() != null) {
            String conditionType = node.getCondition().accept(this);
            if (conditionType != null && !conditionType.equals("Boolean")) {
                errors.add("For condition must be Boolean, got " + conditionType);
            }
        }
        
        if (node.getIncrement() != null) {
            node.getIncrement().accept(this);
        }
        
        node.getBody().accept(this);
        
        currentScope = currentScope.getParent();
        return null;
    }
    
    @Override
    public String visitReturnStatement(ReturnStatement node) {
        if (node.getValue() != null) {
            String returnType = node.getValue().accept(this);
            return returnType;
        }
        return "void";
    }
    
    @Override
    public String visitExpressionStatement(ExpressionStatement node) {
        node.getExpression().accept(this);
        return null;
    }
    
    @Override
    public String visitBlockStatement(BlockStatement node) {
        SymbolTable blockScope = new SymbolTable(currentScope);
        currentScope = blockScope;
        
        for (Statement stmt : node.getStatements()) {
            stmt.accept(this);
        }
        
        currentScope = currentScope.getParent();
        return null;
    }
    
    @Override
    public String visitBinaryExpression(BinaryExpression node) {
        String leftType = node.getLeft().accept(this);
        String rightType = node.getRight().accept(this);
        
        if (leftType == null || rightType == null) {
            return null;
        }
        
        switch (node.getOperator()) {
            case PLUS:
                if (leftType.equals("String") || rightType.equals("String")) {
                    return "String";
                }
                if (isNumericType(leftType) && isNumericType(rightType)) {
                    return getNumericResultType(leftType, rightType);
                }
                break;
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case MODULO:
                if (isNumericType(leftType) && isNumericType(rightType)) {
                    return getNumericResultType(leftType, rightType);
                }
                break;
            case EQUALS:
            case NOT_EQUALS:
            case LESS_THAN:
            case LESS_EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
                return "Boolean";
            case LOGICAL_AND:
            case LOGICAL_OR:
                if (leftType.equals("Boolean") && rightType.equals("Boolean")) {
                    return "Boolean";
                }
                break;
        }
        
        errors.add("Invalid binary operation: " + leftType + " " + node.getOperator() + " " + rightType);
        return null;
    }
    
    @Override
    public String visitUnaryExpression(UnaryExpression node) {
        String operandType = node.getOperand().accept(this);
        
        if (operandType == null) {
            return null;
        }
        
        switch (node.getOperator()) {
            case MINUS:
            case PLUS:
                if (isNumericType(operandType)) {
                    return operandType;
                }
                break;
            case LOGICAL_NOT:
                if (operandType.equals("Boolean")) {
                    return "Boolean";
                }
                break;
        }
        
        errors.add("Invalid unary operation: " + node.getOperator() + " " + operandType);
        return null;
    }
    
    @Override
    public String visitCallExpression(CallExpression node) {
        String calleeType = node.getCallee().accept(this);
        
        for (Expression arg : node.getArguments()) {
            arg.accept(this);
        }
        
        return "Object";
    }
    
    @Override
    public String visitMemberExpression(MemberExpression node) {
        String objectType = node.getObject().accept(this);
        return "Object";
    }
    
    @Override
    public String visitLiteralExpression(LiteralExpression node) {
        return node.getType();
    }
    
    @Override
    public String visitIdentifierExpression(IdentifierExpression node) {
        Symbol symbol = currentScope.lookup(node.getName());
        if (symbol == null) {
            errors.add("Undefined variable '" + node.getName() + "'");
            return null;
        }
        return symbol.getType();
    }
    
    @Override
    public String visitAssignmentExpression(AssignmentExpression node) {
        String targetType = node.getTarget().accept(this);
        String valueType = node.getValue().accept(this);
        
        if (targetType != null && valueType != null) {
            if (!isAssignableFrom(targetType, valueType)) {
                errors.add("Cannot assign " + valueType + " to " + targetType);
            }
        }
        
        return targetType;
    }
    
    private boolean isNumericType(String type) {
        return type.equals("Integer") || type.equals("Decimal");
    }
    
    private String getNumericResultType(String left, String right) {
        if (left.equals("Decimal") || right.equals("Decimal")) {
            return "Decimal";
        }
        return "Integer";
    }
    
    private boolean isAssignableFrom(String target, String source) {
        if (target.equals(source)) {
            return true;
        }
        
        if (target.equals("Decimal") && source.equals("Integer")) {
            return true;
        }
        
        if (target.equals("String")) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String visitAnnotation(Annotation node) {
        return null;
    }
    
    @Override
    public String visitSoqlExpression(SoqlExpression node) {
        return "List<SObject>";
    }
    
    @Override
    public String visitDmlStatement(DmlStatement node) {
        node.getTarget().accept(this);
        return null;
    }
}