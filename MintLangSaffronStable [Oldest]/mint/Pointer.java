/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mint;

/**
 *
 * @author Oliver Chu
 */
public class Pointer {
    public byte type;
    public int value;
    
    public Pointer(byte type, int value) {
        this.type = type;
        this.value = value;
    }
    
    @Override
    public String toString() {
        try {
            return PointerTools.dereferenceAsString(this);
        } catch (MintException ex) {
            return "ERROR";
        }
    }
    
    public String toString2() {
        return "(" + type + ", " + value + ")";
    }
    
    boolean equals(Pointer p) {
        return type == p.type && value == p.value;
    }
}
