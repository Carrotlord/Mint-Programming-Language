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
public class LogicNot extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        Boolean operand0 = PointerTools.dereferenceTruth(arg0);
        if (operand0 == null) {
            throw new MintException("Logical not can only be applied to " + 
                                    "truth values.");
        }
        return Heap.allocateTruth(!operand0);
    }
    
}
