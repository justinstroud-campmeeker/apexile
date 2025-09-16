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

The compiler follows a traditional multi-phase architecture:

1. **Lexer** (`ApexLexer`): Tokenizes input source code
2. **Parser** (`ApexParser`): Builds AST using recursive descent parsing
3. **Semantic Analyzer** (`SemanticAnalyzer`): Type checking and symbol resolution
4. **Code Generator** (`JavaCodeGenerator`): Generates target Java code
5. **CLI Driver** (`ApexCompiler`): Command-line interface and workflow orchestration

## Limitations

This is a simplified Apex compiler with the following limitations:

- No support for advanced Salesforce-specific features (SOQL, DML, etc.)
- Limited standard library support
- No optimization passes
- Basic error reporting
- No support for annotations
- No support for generics
- No support for inner classes

## License

This project is for educational purposes.