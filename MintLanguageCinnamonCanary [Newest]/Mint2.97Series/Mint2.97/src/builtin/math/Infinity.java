package builtin.math;

import builtin.BuiltinSub;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 * Returns infinity, always.
 * @author Oliver Chu
 */
public class Infinity extends BuiltinSub {
    public static final Pointer POS_INF =
        Heap.allocateReal(Double.POSITIVE_INFINITY);
    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        return POS_INF;
    }
}
