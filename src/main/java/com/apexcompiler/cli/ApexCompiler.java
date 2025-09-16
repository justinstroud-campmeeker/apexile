package com.apexcompiler.cli;

import com.apexcompiler.ast.ClassDeclaration;
import com.apexcompiler.codegen.JavaCodeGenerator;
import com.apexcompiler.lexer.ApexLexer;
import com.apexcompiler.lexer.Token;
import com.apexcompiler.parser.ApexParser;
import com.apexcompiler.semantic.SemanticAnalyzer;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ApexCompiler {
    private static final String VERSION = "1.0.0";
    
    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("help")) {
                printHelp(options);
                return;
            }
            
            if (cmd.hasOption("version")) {
                System.out.println("Apex Compiler version " + VERSION);
                return;
            }
            
            String[] files = cmd.getArgs();
            if (files.length == 0) {
                System.err.println("Error: No input files specified");
                printHelp(options);
                System.exit(1);
            }
            
            String inputFile = files[0];
            String outputDir = cmd.getOptionValue("output", ".");
            boolean verbose = cmd.hasOption("verbose");
            boolean checkOnly = cmd.hasOption("check");
            
            compileFile(inputFile, outputDir, verbose, checkOnly);
            
        } catch (ParseException e) {
            System.err.println("Error parsing command line: " + e.getMessage());
            printHelp(options);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Compilation failed: " + e.getMessage());
            if (args.length > 0 && (args[0].equals("-v") || args[0].equals("--verbose"))) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    private static Options createOptions() {
        Options options = new Options();
        
        options.addOption("h", "help", false, "Show this help message");
        options.addOption("v", "version", false, "Show version information");
        options.addOption("o", "output", true, "Output directory (default: current directory)");
        options.addOption("c", "check", false, "Check syntax and semantics only, don't generate code");
        options.addOption("verbose", false, "Enable verbose output");
        
        return options;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("apex-compiler [OPTIONS] <input-file>", 
                          "Compile Apex source code to Java", options, 
                          "\nExample: apex-compiler -o build src/MyClass.apex");
    }
    
    private static void compileFile(String inputFile, String outputDir, boolean verbose, boolean checkOnly) 
            throws IOException {
        
        if (verbose) {
            System.out.println("Compiling " + inputFile + "...");
        }
        
        Path inputPath = Paths.get(inputFile);
        if (!Files.exists(inputPath)) {
            throw new RuntimeException("Input file not found: " + inputFile);
        }
        
        String sourceCode = Files.readString(inputPath);
        
        if (verbose) {
            System.out.println("Lexical analysis...");
        }
        ApexLexer lexer = new ApexLexer(sourceCode);
        List<Token> tokens = lexer.tokenize();
        
        if (verbose) {
            System.out.println("Parsing...");
        }
        ApexParser parser = new ApexParser(tokens);
        ClassDeclaration ast = parser.parseClass();
        
        if (verbose) {
            System.out.println("Semantic analysis...");
        }
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        List<String> errors = analyzer.analyze(ast);
        
        if (!errors.isEmpty()) {
            System.err.println("Semantic errors found:");
            for (String error : errors) {
                System.err.println("  " + error);
            }
            throw new RuntimeException("Compilation failed due to semantic errors");
        }
        
        if (checkOnly) {
            System.out.println("Syntax and semantic analysis completed successfully");
            return;
        }
        
        if (verbose) {
            System.out.println("Code generation...");
        }
        JavaCodeGenerator generator = new JavaCodeGenerator();
        String javaCode = generator.generate(ast);
        
        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        String fileName = inputPath.getFileName().toString();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        Path javaFile = outputPath.resolve(baseName + ".java");
        
        Files.writeString(javaFile, javaCode);
        
        if (verbose) {
            System.out.println("Generated: " + javaFile);
        }
        
        System.out.println("Compilation successful");
    }
}