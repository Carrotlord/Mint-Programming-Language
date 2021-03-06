package builtin.time;

import builtin.BuiltinSub;
import java.util.Date;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class _Date extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        return Heap.allocateString(new Date().toString());
    }
    
}
