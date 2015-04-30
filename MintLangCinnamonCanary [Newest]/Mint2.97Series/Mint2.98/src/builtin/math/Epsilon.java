package builtin.math;

import builtin.BuiltinSub;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 * Returns x times a very small number.
 * @author Oliver Chu
 */
public class Epsilon extends BuiltinSub {
    public static final double EPSILON = 1e-320;
    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Double d = PointerTools.dereferenceReal(args.get(0));
        if (d == null) {
            return Heap.allocateReal(Double.NaN);
        }
        return Heap.allocateReal(d * EPSILON);
    }
}
