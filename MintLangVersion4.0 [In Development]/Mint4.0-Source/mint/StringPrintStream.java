package mint;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * A PrintStream that writes to a String.
 * @author Jiangcheng Oliver Chu
 */
public class StringPrintStream extends PrintStream {
    private String contents;
    
    public StringPrintStream() throws UnsupportedEncodingException {
        super(new ByteArrayOutputStream());
        contents = "";
    }
    
    @Override
    public void print(Object x) {
        genericPrint(x);
    }
    
    @Override
    public void print(String x) {
        genericPrint(x);
    }
    
    @Override
    public void println(Object x) {
        genericPrintln(x);
    }
    
    @Override
    public void println(String x) {
        genericPrintln(x);
    }
    
    public <T> void genericPrint(T t) {
        contents += t;
    }
    
    public <T> void genericPrintln(T t) {
        genericPrint(t + "\n");
    }

    @Override
    public String toString() {
        return contents;
    }
}
