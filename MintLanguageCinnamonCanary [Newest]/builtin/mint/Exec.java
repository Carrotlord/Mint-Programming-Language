package builtin.mint;

import builtin.BuiltinSub;
import mint.Interpreter;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Exec extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String code = PointerTools.dereferenceString(args.get(0));
        Interpreter i = PointerTools.dereferenceInterpreter(args.get(1));
        return i.run(code, true);
    }
    
}
