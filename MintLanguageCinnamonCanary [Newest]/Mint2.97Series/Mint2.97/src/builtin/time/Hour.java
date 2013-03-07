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
public class Hour extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        int hour = new GregorianCalendar().get(Calendar.HOUR);
        if (hour == 0)
            hour = 12;
        return Heap.allocateInt(hour);
    }
    
}
