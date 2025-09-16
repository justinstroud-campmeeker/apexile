package com.apexcompiler.lexer;

public enum TokenType {
    // Keywords
    CLASS,
    PUBLIC,
    PRIVATE,
    PROTECTED,
    STATIC,
    FINAL,
    ABSTRACT,
    VIRTUAL,
    OVERRIDE,
    INTERFACE,
    EXTENDS,
    IMPLEMENTS,
    IF,
    ELSE,
    FOR,
    WHILE,
    DO,
    RETURN,
    BREAK,
    CONTINUE,
    TRY,
    CATCH,
    FINALLY,
    THROW,
    NEW,
    THIS,
    SUPER,
    NULL,
    TRUE,
    FALSE,
    VOID,
    
    // Primitive types
    INTEGER,
    DECIMAL,
    STRING,
    BOOLEAN,
    ID,
    DATE,
    DATETIME,
    TIME,
    
    // Salesforce specific types
    SOBJECT,
    LIST,
    SET,
    MAP,
    
    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %
    ASSIGN,         // =
    PLUS_ASSIGN,    // +=
    MINUS_ASSIGN,   // -=
    MULTIPLY_ASSIGN, // *=
    DIVIDE_ASSIGN,  // /=
    EQUALS,         // ==
    NOT_EQUALS,     // !=
    LESS_THAN,      // <
    LESS_EQUAL,     // <=
    GREATER_THAN,   // >
    GREATER_EQUAL,  // >=
    LOGICAL_AND,    // &&
    LOGICAL_OR,     // ||
    LOGICAL_NOT,    // !
    BITWISE_AND,    // &
    BITWISE_OR,     // |
    BITWISE_XOR,    // ^
    BITWISE_NOT,    // ~
    LEFT_SHIFT,     // <<
    RIGHT_SHIFT,    // >>
    INCREMENT,      // ++
    DECREMENT,      // --
    
    // Delimiters
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    LEFT_BRACKET,   // [
    RIGHT_BRACKET,  // ]
    SEMICOLON,      // ;
    COMMA,          // ,
    DOT,            // .
    QUESTION,       // ?
    COLON,          // :
    
    // Literals
    INTEGER_LITERAL,
    DECIMAL_LITERAL,
    STRING_LITERAL,
    
    // Special
    IDENTIFIER,
    NEWLINE,
    WHITESPACE,
    COMMENT,
    
    // End of file
    EOF
}