package builtin.type;

import builtin.BuiltinSub;
import mint.Constants;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class _String extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        if (arg0.type == Constants.STR_TYPE) {
            return arg0;
        }
        return Heap.allocateString(PointerTools.dereferenceAsString(arg0));
    }
    
}
