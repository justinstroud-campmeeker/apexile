// Generated from Apex source
import java.util.*;

public class HelloWorld {
    public static void main() {
String message = "Hello, World!";        System.debug(message);
    }
    public String greet(String name) {
        if ((name == null)) {
            return "Hello, Anonymous!";
        } else {
            return (("Hello, " + name) + "!");
        }
    }
}
