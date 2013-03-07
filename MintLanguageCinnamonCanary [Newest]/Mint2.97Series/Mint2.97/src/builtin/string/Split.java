package builtin.string;

import builtin.BuiltinSub;
import mint.Constants;
import mint.Heap;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Split extends BuiltinSub {
    private String str;
    
    public Split(String str) {
        this.str = str;
    }
    
    @Override
    public Pointer apply(SmartList<Pointer> args) {
        String[] split;
        if (args.isEmpty() || args.get(0).type == Constants.NULL_TYPE) {
            split = str.split(" ");
        } else {
            String regex = PointerTools.dereferenceString(args.get(0));
            split = str.split(regex);
        }
        SmartList<Pointer> list = new SmartList<Pointer>();
        for (String eachString : split) {
            list.add(Heap.allocateString(eachString));
        }
        return Heap.allocateList(list);
    }
}
