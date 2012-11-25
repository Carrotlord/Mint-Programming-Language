package builtin.graphics;

import builtin.BuiltinSub;
import java.util.ArrayList;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Polygon extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        ArrayList<Integer> dim = new ArrayList<Integer>();
        args = PointerTools.dereferenceList(args.get(0));
        for (Pointer p : args) {
            dim.add(PointerTools.dereferenceInt(p));
        }
        return Heap.allocateShape(new Shape(Shape.POLYGON, dim));
    }
    
}
