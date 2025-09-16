package com.apexcompiler.ast;

public interface ASTVisitor<T> {
    T visitClassDeclaration(ClassDeclaration node);
    T visitMethodDeclaration(MethodDeclaration node);
    T visitVariableDeclaration(VariableDeclaration node);
    T visitIfStatement(IfStatement node);
    T visitWhileStatement(WhileStatement node);
    T visitForStatement(ForStatement node);
    T visitReturnStatement(ReturnStatement node);
    T visitExpressionStatement(ExpressionStatement node);
    T visitBlockStatement(BlockStatement node);
    T visitBinaryExpression(BinaryExpression node);
    T visitUnaryExpression(UnaryExpression node);
    T visitCallExpression(CallExpression node);
    T visitMemberExpression(MemberExpression node);
    T visitLiteralExpression(LiteralExpression node);
    T visitIdentifierExpression(IdentifierExpression node);
    T visitAssignmentExpression(AssignmentExpression node);
}