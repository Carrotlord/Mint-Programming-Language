package mint;

import java.io.IOException;

/**
 * Interface requiring reading input, and writing to normal/debug output.
 * @author Jiangcheng Oliver Chu
 */
interface InputOutputMachine {
    void println(Object o);
    void print(Object o);
    void printerr(Object o);
    void debug(Object o);
    void debugln(Object o);
    void debugerr(Object o);
    String getln() throws IOException;
}
