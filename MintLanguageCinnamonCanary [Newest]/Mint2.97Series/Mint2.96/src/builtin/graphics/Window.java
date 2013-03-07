package builtin.graphics;

import builtin.BuiltinSub;
import javax.swing.JFrame;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Window extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        MintWindow mw = new MintWindow();
        mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mw.setLayout(null);
        return Heap.allocateWindow(mw);
    }
    
}
