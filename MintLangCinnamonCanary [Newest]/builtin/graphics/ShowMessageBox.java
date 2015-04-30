package builtin.graphics;

import builtin.BuiltinSub;
import javax.swing.JOptionPane;
import mint.Constants;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class ShowMessageBox extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String title = PointerTools.dereferenceString(args.get(0));
        String msg = PointerTools.dereferenceString(args.get(1));
        JOptionPane.showMessageDialog(null, msg, title,
                                      JOptionPane.INFORMATION_MESSAGE);
        return new Pointer(Constants.NULL_TYPE, 0);
    }
    
}
