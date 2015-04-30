package builtin.random;

import builtin.BuiltinSub;
import java.util.Random;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class RandomReal extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        return Heap.allocateReal(new Random().nextDouble());
    }
    
}
