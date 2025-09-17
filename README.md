# Apex Compiler

A local Apex language compiler implementation in Java that translates Apex source code to Java.

## Features

- **Lexical Analysis**: Tokenizes Apex source code
- **Parsing**: Builds Abstract Syntax Tree (AST) from tokens
- **Semantic Analysis**: Type checking and symbol resolution
- **Code Generation**: Generates equivalent Java code
- **CLI Interface**: Command-line tool for compilation

## Supported Apex Features

- Class declarations with inheritance and interfaces
- Method declarations with parameters and return types
- Variable declarations and assignments
- Control flow statements (if/else, while, for)
- Expressions and operators
- Basic type system (Integer, Decimal, String, Boolean, etc.)
- Comments (single-line // and block /* */)

## Building

```bash
mvn clean compile
mvn package
```

## Usage

### Command Line Interface

```bash
java -jar target/apex-compiler-1.0.0.jar [OPTIONS] <input-file>
```

#### Options

- `-h, --help`: Show help message
- `-v, --version`: Show version information
- `-o, --output <dir>`: Output directory (default: current directory)
- `-c, --check`: Check syntax and semantics only, don't generate code
- `--verbose`: Enable verbose output

#### Examples

Compile an Apex file to Java:
```bash
java -jar target/apex-compiler-1.0.0.jar examples/Calculator.apex
```

Compile with custom output directory:
```bash
java -jar target/apex-compiler-1.0.0.jar -o build examples/Calculator.apex
```

Check syntax only:
```bash
java -jar target/apex-compiler-1.0.0.jar -c examples/Calculator.apex
```

Verbose compilation:
```bash
java -jar target/apex-compiler-1.0.0.jar --verbose examples/Calculator.apex
```

## Example

Input Apex file (`Calculator.apex`):
```apex
public class Calculator {
    private Integer result;
    
    public Integer add(Integer a, Integer b) {
        result = a + b;
        return result;
    }
}
```

Generated Java file (`Calculator.java`):
```java
// Generated from Apex source
import java.util.*;

public class Calculator {
    private int result;
    
    public int add(int a, int b) {
        result = (a + b);
        return result;
    }
}
```

## Project Structure

```
src/
├── main/java/com/apexcompiler/
│   ├── lexer/          # Lexical analysis
│   ├── parser/         # Syntax analysis
│   ├── ast/            # Abstract Syntax Tree nodes
│   ├── semantic/       # Semantic analysis
│   ├── codegen/        # Code generation
│   └── cli/            # Command-line interface
└── test/java/          # Unit tests
examples/               # Example Apex files
```

## Testing

Run unit tests:
```bash
mvn test
```

Test with example files:
```bash
java -jar target/apex-compiler-1.0.0.jar examples/HelloWorld.apex
java -jar target/apex-compiler-1.0.0.jar examples/Calculator.apex
```

## Architecture

The compiler follows a traditional multi-phase architecture with modern enhancements:

```plantuml
@startuml ApexCompilerArchitecture
!theme plain

title Apex Compiler Architecture & Data Flow

package "Input Layer" {
  [Apex Source Code] as ApexSource
  note top of ApexSource : .apex files with\nclasses, methods, SOQL, DML
}

package "CLI Layer" {
  [ApexCompiler] as CLI
  note right of CLI : Command-line interface\nFile I/O, Error handling\nWorkflow orchestration
}

package "Lexical Analysis" {
  [ApexLexer] as Lexer
  [Token Stream] as Tokens
  
  package "Token Types" {
    [Keywords] as Keywords
    [Operators] as Operators  
    [Literals] as Literals
    [SOQL Literals] as SOQL
    [Annotations] as AnnotTokens
  }
}

package "Syntax Analysis" {
  [ApexParser] as Parser
  [Abstract Syntax Tree] as AST
  
  package "AST Nodes" {
    [ClassDeclaration] as ClassAST
    [MethodDeclaration] as MethodAST
    [Expressions] as ExprAST
    [Statements] as StmtAST
    [Annotations] as AnnotAST
    [SOQL/DML] as SQLDMLAST
    [Generic Types] as GenericAST
  }
}

package "Semantic Analysis" {
  [SemanticAnalyzer] as Semantic
  [Symbol Table] as SymbolTable
  [Type Checker] as TypeChecker
  [Error Reporter] as ErrorReporter
  
  package "Built-in Types" {
    [Salesforce Types] as SFTypes
    [System Classes] as SystemClasses
    [Collections] as Collections
    [Exceptions] as Exceptions
  }
}

package "Code Generation" {
  [JavaCodeGenerator] as CodeGen
  [Type Mapper] as TypeMapper
  [Mock Service Generator] as MockGen
  
  package "Output Generators" {
    [Java Classes] as JavaClasses
    [Annotations] as JavaAnnots
    [SOQL Mocks] as SOQLMocks
    [DML Mocks] as DMLMocks
  }
}

package "Output Layer" {
  [Generated Java Code] as JavaOutput
  [MockDataService] as MockService
  note bottom of JavaOutput : Executable Java classes\nwith mocked Salesforce features
}

' Data Flow
ApexSource --> CLI
CLI --> Lexer : Read .apex files

Lexer --> Keywords
Lexer --> Operators
Lexer --> Literals
Lexer --> SOQL
Lexer --> AnnotTokens
Keywords --> Tokens
Operators --> Tokens
Literals --> Tokens
SOQL --> Tokens
AnnotTokens --> Tokens

Tokens --> Parser : Token stream

Parser --> ClassAST
Parser --> MethodAST
Parser --> ExprAST
Parser --> StmtAST
Parser --> AnnotAST
Parser --> SQLDMLAST
Parser --> GenericAST
ClassAST --> AST
MethodAST --> AST
ExprAST --> AST
StmtAST --> AST
AnnotAST --> AST
SQLDMLAST --> AST
GenericAST --> AST

AST --> Semantic : Validate semantics

Semantic --> SymbolTable : Symbol resolution
Semantic --> TypeChecker : Type validation
Semantic --> ErrorReporter : Error collection
SFTypes --> Semantic
SystemClasses --> Semantic
Collections --> Semantic
Exceptions --> Semantic

Semantic --> CodeGen : Validated AST

CodeGen --> TypeMapper : Map Apex→Java types
CodeGen --> MockGen : Generate mock calls
CodeGen --> JavaClasses
CodeGen --> JavaAnnots
CodeGen --> SOQLMocks
CodeGen --> DMLMocks

JavaClasses --> JavaOutput
JavaAnnots --> JavaOutput
SOQLMocks --> MockService
DMLMocks --> MockService

JavaOutput --> CLI
MockService --> CLI

' Component Details
note right of Lexer
  **Lexical Analysis Features:**
  • Keywords (class, public, static, etc.)
  • Operators (==, !=, &&, ||, etc.)
  • String/Number literals
  • SOQL queries [SELECT...]
  • Annotations @AuraEnabled
  • Generics List<String>
  • Comments // and /* */
end note

note right of Parser
  **Parser Features:**
  • Recursive descent parsing
  • Operator precedence
  • Error recovery
  • Context-sensitive parsing
  • Generic type resolution
  • Annotation parameters
end note

note right of Semantic
  **Semantic Analysis:**
  • Symbol table management
  • Type checking & inference
  • Scope validation
  • Generic type validation
  • Built-in type recognition
  • Error reporting with context
end note

note right of CodeGen
  **Code Generation:**
  • Apex → Java type mapping
  • Mock service integration
  • Annotation preservation
  • Generic type translation
  • SOQL → MockDataService calls
  • DML → Mock operations
end note

' Styling
skinparam package {
  BackgroundColor lightblue
  BorderColor navy
}

skinparam component {
  BackgroundColor lightgreen
  BorderColor darkgreen
}

@enduml
```

### **Component Details:**

1. **Lexer** (`ApexLexer`): Tokenizes input source code with support for SOQL, annotations, and generics
2. **Parser** (`ApexParser`): Builds AST using recursive descent parsing with error recovery
3. **Semantic Analyzer** (`SemanticAnalyzer`): Type checking, symbol resolution, and comprehensive validation
4. **Code Generator** (`JavaCodeGenerator`): Generates target Java code with mock service integration
5. **CLI Driver** (`ApexCompiler`): Command-line interface and workflow orchestration

### **Compilation Flow Example:**

```plantuml
@startuml CompilationFlow
!theme plain

title Apex Compiler - Detailed Compilation Flow

actor User
participant CLI as "ApexCompiler\n(CLI)"
participant Lexer as "ApexLexer"
participant Parser as "ApexParser"
participant Semantic as "SemanticAnalyzer"
participant CodeGen as "JavaCodeGenerator"
participant FileSystem as "File System"

User -> CLI : apex-compiler Calculator.apex
activate CLI

CLI -> FileSystem : Read Calculator.apex
FileSystem -> CLI : Apex source code

note over CLI
**Input Apex Code:**
end note

CLI -> Lexer : Tokenize source
activate Lexer

note over Lexer
**Tokens Generated:**
• AT (@)
• IDENTIFIER (AuraEnabled)
• PUBLIC, CLASS, IDENTIFIER (Calculator)
• PRIVATE, LIST, LESS_THAN, IDENTIFIER (Account), GREATER_THAN
• SOQL_LITERAL ("SELECT Id FROM Account")
• INSERT, IDENTIFIER (accounts)
• etc...
end note

Lexer -> CLI : Token stream
deactivate Lexer

CLI -> Parser : Parse tokens to AST
activate Parser

note over Parser
**AST Structure:**
• ClassDeclaration
  - annotations: [@AuraEnabled]
  - name: "Calculator"
  - fields: [List<Account> accounts]
  - methods: [add(), processData()]
    - SoqlExpression
    - DmlStatement (INSERT)
end note

Parser -> CLI : Abstract Syntax Tree
deactivate Parser

CLI -> Semantic : Analyze semantics
activate Semantic

note over Semantic
**Semantic Analysis:**
✓ Type checking: Integer, List<Account>
✓ Symbol resolution: accounts variable
✓ Built-in type validation: Account, List
✓ SOQL result type: List<SObject>
✓ DML target validation
end note

Semantic -> CLI : Validated AST + Errors
deactivate Semantic

CLI -> CodeGen : Generate Java code
activate CodeGen

note over CodeGen
**Java Generation:**
• @AuraEnabled → @AuraEnabled
• List<Account> → java.util.List<Account>
• SOQL → MockDataService.executeSoql(...)
• DML → MockDataService.insertRecords(...)
• Integer → int (primitive mapping)
end note

CodeGen -> CLI : Generated Java code
deactivate CodeGen

CLI -> FileSystem : Write Calculator.java

note over FileSystem
**Output Java Code:**
end note

CLI -> User : Compilation successful
deactivate CLI

@enduml
```

## Recent Enhancements

✅ **Major improvements have been implemented:**

### ✅ Annotations Support
- Full support for Apex annotations (@AuraEnabled, @TestMethod, etc.)
- Annotation parameters with named and positional arguments
- Proper Java annotation generation

### ✅ Generics Support  
- Generic type parsing (List<String>, Map<Id, Account>, etc.)
- Type parameter resolution and validation
- Java generics generation with proper type mapping

### ✅ SOQL Query Support (Mocked)
- SOQL literal parsing with `[SELECT ... FROM ...]` syntax
- Mock data service for query execution
- Type-safe query result handling

### ✅ DML Operations Support (Mocked)
- Support for `insert`, `update`, `delete`, `upsert` statements
- Mock data service for DML operations
- Proper statement parsing and validation

### ✅ Enhanced Standard Library
- Comprehensive Salesforce type system (Account, Contact, Lead, etc.)
- System classes (System, Database, Test, Schema, etc.)
- Exception hierarchy (DmlException, QueryException, etc.)
- HTTP and JSON utilities

### ✅ Improved Parser
- Better error handling and recovery
- Fixed string literal parsing in return statements
- Enhanced type checking and validation
- Context-sensitive parsing for complex constructs

## Current Limitations

Remaining areas for improvement:

- Inner class support (planned)
- Advanced error reporting with better context
- Code optimization passes
- Some edge cases in complex expression parsing

## License

This project is for educational purposes.