package builtin.system;

import builtin.BuiltinSub;
import mint.Constants;
import mint.Mint;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 * @author Oliver Chu
 */
public class Inveigle extends BuiltinSub {
    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer p = args.get(0);
        if (p.type == Constants.STR_TYPE) {
            Mint.seduced = !Mint.seduced;
            return Constants.MINT_NULL;
        }
        if (p.value == 1 &&
           (p.type == Constants.INT_TYPE || p.type == Constants.TRUTH_TYPE)) {
            Mint.seduced = true;
        } else {
            Mint.seduced = false;
        }
        return Constants.MINT_NULL;
    }
}
