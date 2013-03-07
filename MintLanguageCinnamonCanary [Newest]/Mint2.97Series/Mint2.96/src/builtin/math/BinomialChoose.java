package builtin.math;

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
public class BinomialChoose extends BuiltinSub {
    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        Pointer arg1 = args.get(1);
        int n = PointerTools.dereferenceInt(arg0);
        int k = PointerTools.dereferenceInt(arg1);
        Factorial fact = new Factorial();
        int answer = (int) ((double) fact.factorial(n) /
            ((double)fact.factorial(k) * (double) fact.factorial(n - k)));
        return Heap.allocateInt(answer);
    }
}
