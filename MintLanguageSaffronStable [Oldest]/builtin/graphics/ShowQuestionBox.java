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
public class ShowQuestionBox extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String title = PointerTools.dereferenceString(args.get(0));
        String question = PointerTools.dereferenceString(args.get(1));
        int n = JOptionPane.showConfirmDialog(null, question, title,
                                              JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            return Constants.MINT_TRUE;
        } else {
            return Constants.MINT_FALSE;
        }
    }
    
}
