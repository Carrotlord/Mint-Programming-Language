package builtin.type;

import builtin.BuiltinSub;
import mint.Constants;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class List extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer arg0 = args.get(0);
        if (arg0.type == Constants.LIST_TYPE) {
            return arg0;
        } else if (arg0.type == Constants.STR_TYPE) {
            String s = PointerTools.dereferenceString(arg0);
            SmartList<Pointer> list = new SmartList<Pointer>();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                list.add(Heap.allocateString("" + c));
            }
            return Heap.allocateList(list);
        } else if (arg0.type == Constants.SUBPROGRAM_TYPE) {
            return Heap.allocateList(new SmartList<Pointer>(
                                     PointerTools.dereferenceSub(arg0)));
        } else {
            SmartList<Pointer> list = new SmartList<Pointer>();
            list.add(arg0);
            return Heap.allocateList(list);
        }
    }
    
}
