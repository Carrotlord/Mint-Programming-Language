package builtin.random;

import builtin.BuiltinSub;
import java.util.Random;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class RandomInt extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        Integer a = PointerTools.dereferenceInt(args.get(0));
        Integer b = PointerTools.dereferenceInt(args.get(1));
        return Heap.allocateInt(new Random().nextInt(b - a + 1) + a);
    }
    
}
