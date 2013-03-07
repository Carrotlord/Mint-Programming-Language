package builtin.system;

import builtin.BuiltinSub;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import mint.Constants;
import mint.Mint;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Flourish extends BuiltinSub {
    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        try {
            final PrintStream saved = System.out;
            OutputStreamWriter outputStream =
                new OutputStreamWriter(System.out, "UTF-8");
            outputStream.write(args.get(0).value);
            outputStream.close();
            System.setOut(saved);
        } catch (Throwable tossed) {
            System.out.println(tossed);
            Mint.printStackTrace(tossed.getStackTrace());
        }
        return Constants.MINT_TRUE;
    }
}
