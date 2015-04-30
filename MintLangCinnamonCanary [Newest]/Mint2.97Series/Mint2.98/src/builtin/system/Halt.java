/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package builtin.system;

import builtin.BuiltinSub;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class Halt extends BuiltinSub {
    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        System.exit(1);
        return null;
    }
}
