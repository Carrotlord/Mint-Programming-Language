package builtin.encryption;

import builtin.*;
import mint.Heap;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Jumble extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) {
        String toBeJumbled = PointerTools.dereferenceString(args.get(0));
        String jumbled = "";
        SmartList<Pointer> retnList = new SmartList<Pointer>();
        SmartList<Byte> bytes = new SmartList<Byte>();
        // TODO Finish this method.
        return Heap.allocateList(retnList);
    }
}
