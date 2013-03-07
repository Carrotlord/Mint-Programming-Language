package builtin.file;

import builtin.BuiltinSub;
import java.io.File;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class FileLength extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String fileName = PointerTools.dereferenceString(args.get(0));
        File f = new File(fileName);
        return Heap.allocateInt((int)f.length());
    }
    
}
