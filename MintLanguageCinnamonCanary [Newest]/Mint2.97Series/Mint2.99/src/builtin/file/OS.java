package builtin.file;

import builtin.BuiltinSub;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 * A simple function that returns an object, containing ls, woman, etc...
 * Named simply as listCurrentFiles, briefHelp, fileSysCheck, folderSize,
 * numSymbolicLinks, osName, and more.
 * Allows the user to interface with keys, mouse clicks, clipboard, and more.
 * You may want to have a double system.
 * An object oriented system (this one) and a functional system.
 * NOTICE THAT this OS object is important. It can internally have a
 * "current working directory" (or more than one), it can remember tagged
 * files, it can store temporary data not associated with any file, it can
 * have databases and then save them to the real filesystem.
 * 
 * For a functional version of "cd ..", known in Mint as change-folder("up")
 * or simply OS_object.parent() - better yet OS_object.goUp()
 * @author Oliver Chu
 */
public class OS extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        return null;
    }
    
}
