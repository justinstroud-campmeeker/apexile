package com.apexcompiler.codegen;

import com.apexcompiler.ast.*;
import com.apexcompiler.lexer.TokenType;

import java.util.StringJoiner;

public class JavaCodeGenerator implements ASTVisitor<String> {
    private StringBuilder output;
    private int indentLevel = 0;
    
    public String generate(ClassDeclaration classDecl) {
        output = new StringBuilder();
        classDecl.accept(this);
        return output.toString();
    }
    
    private void indent() {
        for (int i = 0; i < indentLevel; i++) {
            output.append("    ");
        }
    }
    
    private void newLine() {
        output.append("\n");
    }
    
    private String apexToJavaType(String apexType) {
        switch (apexType) {
            case "Integer": return "int";
            case "Decimal": return "double";
            case "String": return "String";
            case "Boolean": return "boolean";
            case "void": return "void";
            case "List": return "java.util.List";
            case "Set": return "java.util.Set";
            case "Map": return "java.util.Map";
            default: return apexType;
        }
    }
    
    @Override
    public String visitClassDeclaration(ClassDeclaration node) {
        output.append("// Generated from Apex source\n");
        output.append("import java.util.*;\n\n");
        
        if (node.getModifiers().contains("public")) {
            output.append("public ");
        }
        
        output.append("class ").append(node.getName());
        
        if (node.getSuperClass() != null) {
            output.append(" extends ").append(node.getSuperClass());
        }
        
        if (!node.getInterfaces().isEmpty()) {
            output.append(" implements ");
            StringJoiner joiner = new StringJoiner(", ");
            for (String iface : node.getInterfaces()) {
                joiner.add(iface);
            }
            output.append(joiner.toString());
        }
        
        output.append(" {\n");
        indentLevel++;
        
        for (VariableDeclaration field : node.getFields()) {
            indent();
            field.accept(this);
            newLine();
        }
        
        if (!node.getFields().isEmpty() && !node.getMethods().isEmpty()) {
            newLine();
        }
        
        for (MethodDeclaration method : node.getMethods()) {
            method.accept(this);
            newLine();
        }
        
        indentLevel--;
        output.append("}\n");
        
        return null;
    }
    
    @Override
    public String visitMethodDeclaration(MethodDeclaration node) {
        indent();
        
        for (String modifier : node.getModifiers()) {
            output.append(modifier).append(" ");
        }
        
        output.append(apexToJavaType(node.getReturnType())).append(" ");
        output.append(node.getName()).append("(");
        
        StringJoiner paramJoiner = new StringJoiner(", ");
        for (MethodDeclaration.Parameter param : node.getParameters()) {
            paramJoiner.add(apexToJavaType(param.getType()) + " " + param.getName());
        }
        output.append(paramJoiner.toString());
        
        output.append(") ");
        
        if (node.getBody() != null) {
            node.getBody().accept(this);
        } else {
            output.append(";\n");
        }
        
        return null;
    }
    
    @Override
    public String visitVariableDeclaration(VariableDeclaration node) {
        for (String modifier : node.getModifiers()) {
            output.append(modifier).append(" ");
        }
        
        output.append(node.getType().toJavaType()).append(" ");
        output.append(node.getName());
        
        if (node.getInitializer() != null) {
            output.append(" = ");
            node.getInitializer().accept(this);
        }
        
        output.append(";");
        return null;
    }
    
    @Override
    public String visitIfStatement(IfStatement node) {
        indent();
        output.append("if (");
        node.getCondition().accept(this);
        output.append(") ");
        
        if (node.getThenBranch() instanceof BlockStatement) {
            node.getThenBranch().accept(this);
        } else {
            output.append("{\n");
            indentLevel++;
            indent();
            node.getThenBranch().accept(this);
            newLine();
            indentLevel--;
            indent();
            output.append("}");
        }
        
        if (node.getElseBranch() != null) {
            output.append(" else ");
            if (node.getElseBranch() instanceof BlockStatement) {
                node.getElseBranch().accept(this);
            } else {
                output.append("{\n");
                indentLevel++;
                indent();
                node.getElseBranch().accept(this);
                newLine();
                indentLevel--;
                indent();
                output.append("}");
            }
        }
        
        newLine();
        return null;
    }
    
    @Override
    public String visitWhileStatement(WhileStatement node) {
        indent();
        output.append("while (");
        node.getCondition().accept(this);
        output.append(") ");
        node.getBody().accept(this);
        newLine();
        return null;
    }
    
    @Override
    public String visitForStatement(ForStatement node) {
        indent();
        output.append("for (");
        
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        } else {
            output.append(";");
        }
        
        output.append(" ");
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        output.append("; ");
        
        if (node.getIncrement() != null) {
            node.getIncrement().accept(this);
        }
        
        output.append(") ");
        node.getBody().accept(this);
        newLine();
        return null;
    }
    
    @Override
    public String visitReturnStatement(ReturnStatement node) {
        indent();
        output.append("return");
        if (node.getValue() != null) {
            output.append(" ");
            node.getValue().accept(this);
        }
        output.append(";");
        newLine();
        return null;
    }
    
    @Override
    public String visitExpressionStatement(ExpressionStatement node) {
        indent();
        node.getExpression().accept(this);
        output.append(";");
        newLine();
        return null;
    }
    
    @Override
    public String visitBlockStatement(BlockStatement node) {
        output.append("{\n");
        indentLevel++;
        
        for (Statement stmt : node.getStatements()) {
            stmt.accept(this);
        }
        
        indentLevel--;
        indent();
        output.append("}");
        return null;
    }
    
    @Override
    public String visitBinaryExpression(BinaryExpression node) {
        output.append("(");
        node.getLeft().accept(this);
        output.append(" ");
        
        switch (node.getOperator()) {
            case PLUS: output.append("+"); break;
            case MINUS: output.append("-"); break;
            case MULTIPLY: output.append("*"); break;
            case DIVIDE: output.append("/"); break;
            case MODULO: output.append("%"); break;
            case EQUALS: output.append("=="); break;
            case NOT_EQUALS: output.append("!="); break;
            case LESS_THAN: output.append("<"); break;
            case LESS_EQUAL: output.append("<="); break;
            case GREATER_THAN: output.append(">"); break;
            case GREATER_EQUAL: output.append(">="); break;
            case LOGICAL_AND: output.append("&&"); break;
            case LOGICAL_OR: output.append("||"); break;
            case BITWISE_AND: output.append("&"); break;
            case BITWISE_OR: output.append("|"); break;
            case BITWISE_XOR: output.append("^"); break;
            case LEFT_SHIFT: output.append("<<"); break;
            case RIGHT_SHIFT: output.append(">>"); break;
            default: output.append(node.getOperator().toString()); break;
        }
        
        output.append(" ");
        node.getRight().accept(this);
        output.append(")");
        return null;
    }
    
    @Override
    public String visitUnaryExpression(UnaryExpression node) {
        switch (node.getOperator()) {
            case MINUS: output.append("-"); break;
            case PLUS: output.append("+"); break;
            case LOGICAL_NOT: output.append("!"); break;
            case BITWISE_NOT: output.append("~"); break;
            case INCREMENT: output.append("++"); break;
            case DECREMENT: output.append("--"); break;
            default: output.append(node.getOperator().toString()); break;
        }
        
        node.getOperand().accept(this);
        return null;
    }
    
    @Override
    public String visitCallExpression(CallExpression node) {
        node.getCallee().accept(this);
        output.append("(");
        
        StringJoiner argJoiner = new StringJoiner(", ");
        for (Expression arg : node.getArguments()) {
            StringBuilder argBuilder = new StringBuilder();
            StringBuilder originalOutput = output;
            output = argBuilder;
            arg.accept(this);
            output = originalOutput;
            argJoiner.add(argBuilder.toString());
        }
        output.append(argJoiner.toString());
        
        output.append(")");
        return null;
    }
    
    @Override
    public String visitMemberExpression(MemberExpression node) {
        node.getObject().accept(this);
        output.append(".");
        output.append(node.getProperty());
        return null;
    }
    
    @Override
    public String visitLiteralExpression(LiteralExpression node) {
        if (node.getValue() == null) {
            output.append("null");
        } else if (node.getValue() instanceof String) {
            output.append("\"").append(node.getValue()).append("\"");
        } else if (node.getValue() instanceof Boolean) {
            output.append(node.getValue().toString());
        } else {
            output.append(node.getValue().toString());
        }
        return null;
    }
    
    @Override
    public String visitIdentifierExpression(IdentifierExpression node) {
        output.append(node.getName());
        return null;
    }
    
    @Override
    public String visitAssignmentExpression(AssignmentExpression node) {
        node.getTarget().accept(this);
        
        switch (node.getOperator()) {
            case ASSIGN: output.append(" = "); break;
            case PLUS_ASSIGN: output.append(" += "); break;
            case MINUS_ASSIGN: output.append(" -= "); break;
            case MULTIPLY_ASSIGN: output.append(" *= "); break;
            case DIVIDE_ASSIGN: output.append(" /= "); break;
            default: output.append(" = "); break;
        }
        
        node.getValue().accept(this);
        return null;
    }
    
    @Override
    public String visitAnnotation(Annotation node) {
        output.append("@").append(node.getName());
        if (!node.getValues().isEmpty()) {
            output.append("(");
            StringJoiner valueJoiner = new StringJoiner(", ");
            for (Annotation.AnnotationValue value : node.getValues()) {
                if (value.getName() != null) {
                    valueJoiner.add(value.getName() + " = " + formatAnnotationValue(value.getValue()));
                } else {
                    valueJoiner.add(formatAnnotationValue(value.getValue()));
                }
            }
            output.append(valueJoiner.toString());
            output.append(")");
        }
        return null;
    }
    
    private String formatAnnotationValue(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else {
            return value.toString();
        }
    }
    
    @Override
    public String visitSoqlExpression(SoqlExpression node) {
        output.append("MockDataService.executeSoql(\"");
        output.append(node.getQuery().replace("\"", "\\\""));
        output.append("\")");
        return null;
    }
    
    @Override
    public String visitDmlStatement(DmlStatement node) {
        indent();
        switch (node.getOperation()) {
            case INSERT:
                output.append("MockDataService.insertRecords(");
                break;
            case UPDATE:
                output.append("MockDataService.updateRecords(");
                break;
            case DELETE:
                output.append("MockDataService.deleteRecords(");
                break;
            case UPSERT:
                output.append("MockDataService.upsertRecords(");
                break;
        }
        node.getTarget().accept(this);
        output.append(");");
        newLine();
        return null;
    }
}