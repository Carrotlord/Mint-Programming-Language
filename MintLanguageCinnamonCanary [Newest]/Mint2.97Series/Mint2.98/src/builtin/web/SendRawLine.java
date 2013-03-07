package builtin.web;

import builtin.BuiltinSub;
import mint.Constants;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class SendRawLine extends BuiltinSub {
    private IRCBot bot;
    
    public void setBot(IRCBot b) {
        bot = b;
    }

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        //String chan = PointerTools.dereferenceString(args.get(0));
        String msg = PointerTools.dereferenceString(args.get(0));
        bot.sendRawLine(msg);
        return new Pointer(Constants.NULL_TYPE, 0);
    }
    
}
