/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mint;

import java.util.HashMap;

/**
 *
 * @author Oliver Chu
 */
public class MintObject {
    private HashMap<String, Pointer> mapping;
    
    MintObject() {
        mapping = new HashMap<String, Pointer>();
    }
    
    SmartList<String> keys() {
        return new SmartList<String>(mapping.keySet());
    }
    
    void put(String name, Pointer value) {
        mapping.put(name, value);
    }
    
    void remove(String name) {
        mapping.remove(name);
    }
    
    boolean containsName(String name) {
        return mapping.containsKey(name);
    }
    
    Pointer get(String name) {
        return mapping.get(name);
    }
    
    SmartList<Pointer> values() {
        return new SmartList<Pointer>(mapping.values());
    }
    
    HashMap<String, Pointer> getMapping() {
        return mapping;
    }
    
    boolean equals(MintObject m) {
        return mapping.equals(m.getMapping());
    }
    
    @Override
    public String toString() {
        return "object" + mapping.toString().replace("=", ": ");
    }
    
    public void putAll(MintObject mo) {
        HashMap<String, Pointer> map = mo.getMapping();
        mapping.putAll(map);
    }
}
