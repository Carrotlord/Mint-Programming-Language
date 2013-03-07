package builtin.system;

import builtin.BuiltinSub;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Swap extends BuiltinSub {
    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer firstReal = args.get(0);
        Pointer secondReal = args.get(1);
        Heap.swapReals(firstReal, secondReal);
        SmartList<Pointer> swapped = new SmartList<Pointer>();
        swapped.add(secondReal);
        swapped.add(firstReal);
        return Heap.allocateList(swapped);
    }
}
