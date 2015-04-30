package mint;

/**
 *
 * @author Jiangcheng Oliver Chu
 */
class MintValue {
    private char type;
    private int address;
    private char heapId;
    
    public static final char MAIN_HEAP = 0;
    public static final char INT_TYPE = 0;
    public static final char DBL_TYPE = 1;
    public static final char STR_TYPE = 2;
    public static final char OBJ_TYPE = 3;

    public MintValue(char kind, int ref, char id) {
        type = kind;
        address = ref;
        heapId = id;
    }
    
    public int dereferenceInt() {
        // TODO : finish this method
        return -1;
    }
    
    public double dereferenceDouble() {
        // TODO : finish this method
        return -1.0;
    }
    
    public String dereferenceString() {
        // TODO : finish this method
        return "";
    }
    
    public MintObject dereferenceObj() {
        // TODO : finish this method
        return new MintObject();
    }
}
