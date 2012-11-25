/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package builtin.file;

import builtin.BuiltinSub;
import mint.Heap;
import mint.MintException;
import mint.NumberTools;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class HexToBytes extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String hex = PointerTools.dereferenceString(args.get(0));
        byte[] b = NumberTools.hexToBytes(hex);
        SmartList<Byte> bytes = new SmartList<Byte>();
        for (int i = 0; i < b.length; i++) {
            bytes.add(b[i]);
        }
        return Heap.allocateBytes(bytes);
    }
    
}
