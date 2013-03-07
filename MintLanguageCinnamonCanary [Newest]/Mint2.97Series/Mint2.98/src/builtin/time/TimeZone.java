package builtin.time;

import builtin.BuiltinSub;
import java.util.GregorianCalendar;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class TimeZone extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        return Heap.allocateString(new GregorianCalendar().getTimeZone().
                                   getDisplayName());
    }
    
}
