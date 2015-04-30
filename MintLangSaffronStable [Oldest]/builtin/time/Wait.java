package builtin.time;

import builtin.BuiltinSub;
import mint.Constants;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Wait extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        int milliseconds = PointerTools.dereferenceInt(args.get(0));      
        long nanos = (long)milliseconds * 1000000;
        long startTime = System.nanoTime();
        while ((System.nanoTime() - startTime) < nanos) {
            // do absolutely nothing.
            // System.out.println(System.nanoTime() - startTime);
        }
/*        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            // Ignore.
        } */
        return Constants.MINT_NULL;
    }
    
}
