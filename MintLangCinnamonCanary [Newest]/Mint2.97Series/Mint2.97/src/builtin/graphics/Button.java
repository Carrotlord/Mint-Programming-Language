package builtin.graphics;

import builtin.BuiltinSub;
import mint.Heap;
import mint.Interpreter;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Button extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Interpreter i = PointerTools.dereferenceInterpreter(args.get(0));
        return Heap.allocateButton(new ButtonManager(i));
    }
    
}
