import com.apexcompiler.lexer.*;
import java.util.List;

public class DebugLexer {
    public static void main(String[] args) {
        String source = "if (name == null) {\n    return 'Hello';\n}";
        ApexLexer lexer = new ApexLexer(source);
        List<Token> tokens = lexer.tokenize();
        
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}