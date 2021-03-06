package builtin.web;

import builtin.BuiltinSub;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import mint.Heap;
import mint.MintException;
import mint.Pointer;
import mint.PointerTools;
import mint.SmartList;

/**
 *
 * @author Oliver Chu
 */
public class GetWebsiteContents extends BuiltinSub {
    
    private String addProtocol(String url) {
        if (!(url.startsWith("http://") || url.startsWith("https://")))
            return "http://" + url;
        return url;
    }

    @Override
    public Pointer apply(SmartList<Pointer> args) throws MintException {
        String url = PointerTools.dereferenceString(args.get(0));
        url = addProtocol(url);
        URL u;
        try {
            u = new URL(url);
        } catch (MalformedURLException ex) {
            throw new MintException("Badly formatted URL: " + url);
        }
        BufferedReader website;
        try {
            website = new BufferedReader(new InputStreamReader(u.openStream()));
        } catch (IOException ex) {
            throw new MintException("IO Exception while reading from URL: " +
                                    url);
        }
        String lines = "";
        String line = "";
        while (true) {
            try {
                line = website.readLine();
            } catch (IOException ex) {
                throw new MintException("IO Exception while reading from URL: "
                                        + url);
            }
            if (line == null)
                break;
            lines += line + "\n";
        }
        try {
            website.close();
        } catch (IOException ex) {
            throw new MintException("IO Exception while closing URL: " + url);
        }
        return Heap.allocateString(lines);
    }
    
}
