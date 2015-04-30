package builtin;

import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public abstract class BuiltinSub {
    public abstract Pointer apply(SmartList<Pointer> args) throws MintException;
}
