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
        //reportMaxHeapSizes();
        runTests();
    }
    
    public static void reportMaxHeapSizes() {
        println(SuccessorVirtualMachine.reportMaximumHeapSize(
                SuccessorVirtualMachine.LINEAR_GROWTH, 2048));
        println(SuccessorVirtualMachine.reportMaximumHeapSize(
                SuccessorVirtualMachine.QUADRATIC_GROWTH, 2048));
        println(SuccessorVirtualMachine.reportMaximumHeapSize(
                SuccessorVirtualMachine.EXPONENTIAL_GROWTH, 2047));
    }
    
    private static void runTests() {
        TestGroup[] allTests = {new AssemblerTests(), new SuccessorTests()};
        (new TestRunner(allTests)).runTests();
    }
    
    /** Prints object to console; can be redirected to a file or other output.
     * @param o object to be printed
     */
    public static void println(Object o) {
        System.out.println(o);
    }
    
    public static void print(Object o) {
        System.out.print(o);
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
    
    public static void debugerr(Object o) {
        System.err.println("DEBUG: " + o);
    }
}
