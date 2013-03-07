package builtin.operator;

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
public class LogicOr extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        Pointer arg1 = args.get(1);
        Boolean operand0 = PointerTools.dereferenceTruth(arg0);
        Boolean operand1 = PointerTools.dereferenceTruth(arg1);
        if (operand0 == null || operand1 == null) {
            throw new MintException("Logical or can only be applied to " + 
                                    "truth values.");
        }
        return Heap.allocateTruth(operand0 || operand1);
    }
    
}
