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
public class AppendStrToFile extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String fileName = PointerTools.dereferenceString(args.get(0));
        String contents = PointerTools.dereferenceString(args.get(1));
        FileIO.appendStrToFile(contents, fileName);
        return new Pointer(Constants.NULL_TYPE, 0);
    }
    
}
