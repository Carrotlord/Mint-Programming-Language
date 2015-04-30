package builtin.time;

import builtin.BuiltinSub;
import java.util.Calendar;
import java.util.GregorianCalendar;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Millisecond extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        return Heap.allocateInt(new GregorianCalendar().get(
                                Calendar.MILLISECOND));
    }
    
}
