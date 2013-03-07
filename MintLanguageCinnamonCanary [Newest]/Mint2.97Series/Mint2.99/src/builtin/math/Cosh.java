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
public class Cosh extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        arg0 = PointerTools.convertPreciseRealToReal(arg0);
        Double operand0 = PointerTools.dereferenceReal(arg0);
        if (operand0 == null) {
            throw new MintException(
                      "Hyperbolic cosine can only be applied to integers" + 
                      " or reals.");
        }
        return Heap.allocateReal(Math.cosh(operand0));
    }
    
}
