package mint;

import java.util.ArrayList;

/**
 * @author Oliver Chu
 */
public class Table extends ArrayList {
    ArrayList<SmartList<Pointer>> contents =
                       new ArrayList<SmartList<Pointer>>();
    
    public void addBinding(SmartList<Pointer> slp) {
        contents.add(slp);
    }
    
    @Override
    public SmartList<Pointer> get(int i) {
        return contents.get(i);
    }
    
    public Pointer getValue(Pointer key) {
        for (SmartList<Pointer> slp : contents) {
            if (slp.get(0).toString().equals(key.toString())) {
                return slp.get(1);
            }
        }
        return Constants.MINT_NULL;
    }
    
    @Override
    public String toString() {
        return contents.toString().replace("[", "{").replace("]", "}");
    }
}
