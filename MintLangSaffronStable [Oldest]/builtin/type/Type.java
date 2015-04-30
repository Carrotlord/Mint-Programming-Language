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
public class Type extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Pointer p = args.get(0);
        switch (p.type) {
            case Constants.INT_TYPE:
                return Heap.allocateString("int");
            case Constants.REAL_TYPE:
                return Heap.allocateString("real");
            case Constants.STR_TYPE:
                return Heap.allocateString("string");
            case Constants.TRUTH_TYPE:
                return Heap.allocateString("truth");
            case Constants.NULL_TYPE:
                return Heap.allocateString("null");
            case Constants.MESSAGE_TYPE:
                return Heap.allocateString("message");
            case Constants.OBJECT_TYPE:
                return Heap.allocateString("object");
            case Constants.LIST_TYPE:
                return Heap.allocateString("list");
            case Constants.SUBPROGRAM_TYPE:
                return Heap.allocateString("subprogram");
            case Constants.BLOCK_TYPE:
                return Heap.allocateString("block");
            case Constants.BIG_INT_TYPE:
                return Heap.allocateString("bigint");
            case Constants.PRECISE_REAL_TYPE:
                return Heap.allocateString("precisereal");
            case Constants.BYTES_TYPE:
                return Heap.allocateString("bytes");
            case Constants.WINDOW_TYPE:
                return Heap.allocateString("window");
            case Constants.BUTTON_TYPE:
                return Heap.allocateString("button");
            case Constants.INTERPRETER_TYPE:
                return Heap.allocateString("interpreter");
            case Constants.SHAPE_TYPE:
                return Heap.allocateString("shape");
            case Constants.KEYWORD_TYPE:
                if (PointerTools.dereferenceKeyword(p) == Constants.ELLIPSIS)
                    return Heap.allocateString("ellipsis");
                else
                    return Heap.allocateString("keyword");
            default:
                return Heap.allocateString("unknown");
        }
    }
    
}
