package mint;

import java.util.HashMap;

/**
 * Mint object representing both user objects and environment frames.
 * @author Jiangcheng Oliver Chu
 */
class MintObject {
    private HashMap<String, MintValue> bindings;
    
    public MintObject() {
        bindings = new HashMap<String, MintValue>();
    }
    
    public void assign(String variable, MintValue value) {
        bindings.put(variable, value);
    }
    
    public MintValue get(String variable) throws MintException {
        if (bindings.containsKey(variable)) {
            return bindings.get(variable);
        } else {
            throw new MintException("Variable " + variable +
                                    " used before assignment.");
        }
    }
    
    /**
     * Deletes the key variable and the value associated with it.
     * @param variable the string name of the variable
     * @return false iff variable was not defined before deletion.
     */
    public boolean delete(String variable) {
        MintValue removed = bindings.remove(variable);
        return removed == null;
    }
    
    public void deleteAll() {
        bindings.clear();
    }
}
