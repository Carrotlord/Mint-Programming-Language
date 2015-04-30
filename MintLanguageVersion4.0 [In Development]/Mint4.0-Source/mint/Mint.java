package mint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import test.*;

/**
 * Mint Programming Language, version 4 - the "compiled to bytecode" edition.
 * @author Jiangcheng Oliver Chu
 */
public class Mint {
    public static final BufferedReader KEYBOARD =
         new BufferedReader(new InputStreamReader(System.in));

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        runTests();
    }
    
    private static void runTests() {
        TestGroup[] allTests = {new AssemblerTests()};
        (new TestRunner(allTests)).runTests();
    }
    
    /** Prints object to console; can be redirected to a file or other output.
     * @param o object to be printed
     */
    public static void println(Object o) {
        System.out.println(o);
    }
    
    public static void printerr(Object o) {
        System.err.println(o);
    }
    
    /** Reads line from console.
     * @return user input
     * @throws IOException 
     */
    public static String getln() throws IOException {
        return KEYBOARD.readLine();
    }
    
    public static void debugln(Object o) {
        System.out.println("DEBUG: " + o);
    }
}
