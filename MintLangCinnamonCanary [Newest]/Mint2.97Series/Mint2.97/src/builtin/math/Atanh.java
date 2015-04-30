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
public class Atanh extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        arg0 = PointerTools.convertPreciseRealToReal(arg0);
        Double operand0 = PointerTools.dereferenceReal(arg0);
        if (operand0 == null) {
            throw new MintException(
                      "Hyperbolic arctangent can only be applied to integers" + 
                      " or reals.");
        }
        double x = operand0;
        return Heap.allocateReal(Math.log(Math.sqrt(1 + x) / Math.sqrt(1 - x)));
    }
    
}
