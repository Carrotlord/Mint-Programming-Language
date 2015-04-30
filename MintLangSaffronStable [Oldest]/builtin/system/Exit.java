package builtin.system;

import builtin.BuiltinSub;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Exit extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        System.exit(0);
        return null;
    }
    
}
