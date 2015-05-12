package mint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import test.*;

/**
 * Mint Programming Language, version 4 - the "compiled to bytecode" edition.
 * @author Jiangcheng Oliver Chu
 */
public class Mint implements InputOutputMachine {
    private BufferedReader keyboard;
    private PrintStream output;
    private InputStream input;
    public static final Mint IO = new Mint();
    
    public Mint(InputStream in, PrintStream out) {
        input = in;
        output = out;
        keyboard = new BufferedReader(new InputStreamReader(input));
    }
    
    public Mint(PrintStream out) {
        this(System.in, out);
    }
    
    public Mint() {
        this(System.in, System.out);
    }
    
    public void setPrintStream(StringPrintStream out) {
        output = out;
    }
    
    public void setSystemPrintStream() {
        output = System.out;
    }
    
    public String inspectPrintOutput() {
        return output.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //reportMaxHeapSizes();
        runTests();
    }
    
    public void reportMaxHeapSizes() {
        IO.println(SuccessorVirtualMachine.reportMaximumHeapSize(
                SuccessorVirtualMachine.LINEAR_GROWTH, 2048));
        IO.println(SuccessorVirtualMachine.reportMaximumHeapSize(
                SuccessorVirtualMachine.QUADRATIC_GROWTH, 2048));
        IO.println(SuccessorVirtualMachine.reportMaximumHeapSize(
                SuccessorVirtualMachine.EXPONENTIAL_GROWTH, 2047));
    }
    
    private static void runTests() {
        TestGroup[] allTests = {
            new AssemblerTests(),
            new SuccessorTests(),
            new DataTests()
        };
        (new TestRunner(allTests)).runTests();
    }
    
    /** Prints object to console; can be redirected to a file or other output.
     * @param o object to be printed
     */
    public void println(Object o) {
        output.println(o);
    }
    
    public void print(Object o) {
        output.print(o);
    }
    
    public void printerr(Object o) {
        System.err.println(o);
    }
    
    /** Reads line from console.
     * @return user input
     * @throws IOException 
     */
    public String getln() throws IOException {
        return keyboard.readLine();
    }
    
    public void debug(Object o) {
        output.print("DEBUG: " + o);
    }
    
    public void debugln(Object o) {
        output.println("DEBUG: " + o);
    }
    
    public void debugerr(Object o) {
        System.err.println("DEBUG: " + o);
    }
}
