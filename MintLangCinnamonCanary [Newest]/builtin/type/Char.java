package builtin.type;

import builtin.BuiltinSub;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Char extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        int i = PointerTools.dereferenceInt(args.get(0));
        return Heap.allocateString("" + (char)i);
    }
    
}
