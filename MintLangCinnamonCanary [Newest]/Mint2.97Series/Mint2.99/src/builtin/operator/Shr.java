package builtin.operator;

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
public class Shr extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        Pointer arg1 = args.get(1);
        if (arg0.type == Constants.INT_TYPE && arg1.type == Constants.INT_TYPE)
        {
            int operand0 = PointerTools.dereferenceInt(arg0);
            int operand1 = PointerTools.dereferenceInt(arg1);
            return Heap.allocateInt(operand0 >>> operand1);
        } else {
            throw new MintException("Shift right can only be applied to " + 
                                    "integers.");
        }
    }
    
}
