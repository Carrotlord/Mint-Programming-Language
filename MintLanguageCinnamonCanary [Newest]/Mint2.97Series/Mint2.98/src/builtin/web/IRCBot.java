package builtin.web;

import mint.Environment;
import mint.Heap;
import mint.Interpreter;
import mint.MintException;
import mint.Pointer;
import mint.SmartList;
import mint.Subprogram;
import org.jibble.pircbot.*;

public class IRCBot extends PircBot {
    private Subprogram commandsSub;
    private String name;
    private String server;
    private boolean verbose;
    private SmartList<String> chans;
    private Interpreter i;
    
    public IRCBot(String name, Subprogram commandsSub, String server,
                  boolean verbose, SmartList<String> chans,
                  Interpreter interp) {
        this.setName(name);
        this.commandsSub = commandsSub;
        this.name = name;
        this.server = server;
        this.verbose = verbose;
        this.chans = chans;
        i = interp;
    }
    
    @Override
    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {
        SmartList<Pointer> argList = new SmartList<Pointer>();
        argList.add(Heap.allocateString(channel));
        argList.add(Heap.allocateString(sender));
        argList.add(Heap.allocateString(login));
        argList.add(Heap.allocateString(hostname));
        argList.add(Heap.allocateString(message));
        Interpreter interpreter = i;
        Environment env = i.getEnv();
        SmartList<Pointer> sendMsgArgs = new SmartList<Pointer>();
        sendMsgArgs.add(Heap.allocateName("channel"));
        sendMsgArgs.add(Heap.allocateName("msg"));
        SendMessage sm = new SendMessage();
        sm.setBot(this);
        env.put("sendMessage", Heap.allocateSub(new Subprogram("sendMessage",
                               sendMsgArgs, sm)));
        SmartList<Pointer> sendRawLineArgs = new SmartList<Pointer>();
        sendRawLineArgs.add(Heap.allocateName("msg"));
        SendRawLine srl = new SendRawLine();
        srl.setBot(this);
        env.put("sendRawLine", Heap.allocateSub(
                new Subprogram("sendRawLine", sendRawLineArgs, srl)));
        SmartList<String> imports = i.getImports();
        try {
            commandsSub.execute(env, imports, argList, interpreter);
        } catch (MintException ex) {
            System.err.println(ex.getMessage());
        }
/*        if (message.equalsIgnoreCase("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": The time is now " + time);
        } else if (message.equalsIgnoreCase("pe")) {
            sendMessage(channel, channel + ":" + sender + ":" + login + ":" +
                                 hostname + ":" + message);
        }
 */ 
    }
}
