package builtin.file;

import builtin.BuiltinSub;
import mint.Constants;
import mint.FileIO;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class MakeFolder extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String folderName = PointerTools.dereferenceString(args.get(0));
        FileIO.makeFolder(folderName);
        return new Pointer(Constants.NULL_TYPE, 0);
    }
    
}
