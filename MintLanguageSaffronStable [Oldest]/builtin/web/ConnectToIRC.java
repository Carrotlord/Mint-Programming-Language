package builtin.web;

import builtin.BuiltinSub;
import java.io.IOException;
import mint.Constants;
import mint.Interpreter;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;
import mint.Subprogram;
import org.jibble.pircbot.*;

/**
 *
 * @author Oliver Chu
 */
public class ConnectToIRC extends BuiltinSub {

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String server = PointerTools.dereferenceString(args.get(0));
        String botName = PointerTools.dereferenceString(args.get(1));
        SmartList<Pointer> channels = PointerTools.dereferenceList(
                                      args.get(2));
        boolean showDebug = PointerTools.dereferenceTruth(args.get(3));
        Subprogram commandsSub = PointerTools.dereferenceSub(args.get(4));
        Interpreter interp = PointerTools.dereferenceInterpreter(args.get(5));
        SmartList<String> chans = new SmartList<String>();
        for (Pointer p : channels) {
            chans.add(PointerTools.dereferenceString(p));
        }
        // Now start our bot up.
        IRCBot bot = new IRCBot(botName, commandsSub, server, showDebug, chans,
                                interp);
        // Enable debugging output, or not.
        bot.setVerbose(showDebug);
        try {
            try {
                // Connect to the IRC server.
                bot.connect(server);
            //Note: Ignore this error from Netbeans. It doesn't exist. Code
            //still compiles.
            } catch (IrcException ex) {
                throw new MintException("IRC Exception.");
            }
        } catch (IOException ex) {
            throw new MintException(
                  "IO Exception while connecting to IRC server " + server);
        } catch (Throwable ex) {
            
        }
        for (String c : chans) {
            bot.joinChannel(c);
        }
        return new Pointer(Constants.NULL_TYPE, 0);
    }
    
}
