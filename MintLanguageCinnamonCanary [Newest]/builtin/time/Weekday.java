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
public class Weekday extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return Heap.allocateString(weekdays[new GregorianCalendar().get(
                                   Calendar.DAY_OF_WEEK) - 1]);
    }
    
}
