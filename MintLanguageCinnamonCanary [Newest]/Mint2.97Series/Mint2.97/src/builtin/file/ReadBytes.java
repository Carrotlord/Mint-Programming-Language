package builtin.file;

import builtin.BuiltinSub;
import mint.FileIO;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class ReadBytes extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String fileName = PointerTools.dereferenceString(args.get(0));
        int begin = PointerTools.dereferenceInt(args.get(1));
        int end = PointerTools.dereferenceInt(args.get(2));
        return Heap.allocateBytes(FileIO.readBytes(fileName, begin, end));
    }
    
}
