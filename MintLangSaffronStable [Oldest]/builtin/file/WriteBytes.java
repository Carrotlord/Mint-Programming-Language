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
public class WriteBytes extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String fileName = PointerTools.dereferenceString(args.get(0));
        SmartList<Byte> bytes = PointerTools.dereferenceBytes(args.get(1));
        int begin = PointerTools.dereferenceInt(args.get(2));
        FileIO.writeBytes(fileName, bytes, begin);
        return new Pointer(Constants.NULL_TYPE, 0);
    }
    
}
