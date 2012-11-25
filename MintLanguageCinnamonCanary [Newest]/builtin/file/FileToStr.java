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
public class FileToStr extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String fileName = PointerTools.dereferenceString(args.get(0));
        return Heap.allocateString(FileIO.fileToStr(fileName));
    }
    
}
