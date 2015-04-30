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
public class BytesToHex extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        SmartList<Byte> bytes = PointerTools.dereferenceBytes(args.get(0));
        byte[] b = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            b[i] = bytes.get(i);
        }
        return Heap.allocateString(NumberTools.bytesToHex(b));
    }
    
}
