package builtin.operator;

import builtin.BuiltinSub;
import mint.Constants;
import mint.MintException;
import mint.Operator;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class LesserEq extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        Pointer arg1 = args.get(1);
        return Operator.comparisonFamily(Constants.LESS_OR_EQUAL, arg0, arg1);
    }
    
}
