package mint;

/**
 * Virtual machine that executes successor code and has access to console
 * and file IO on the actual system.
 * @author Jiangcheng Oliver Chu
 */
public class SuccessorVirtualMachine {
    private int[][] intHeapSegments;
    private double[][] doubleHeapSegments;
    private String[][] stringHeapSegments;
    private MintObject[][] mintObjHeapSegments;
    private int numSegments;
    private int initialSlots;
    private char growthRate;
    private int latestActiveSegment;
    
    public static final char LINEAR_GROWTH = 0;
    public static final char QUADRATIC_GROWTH = 1;
    public static final char EXPONENTIAL_GROWTH = 2;
    
    private static final int DEFAULT_SEGMENTS = 14;
    private static final char DEFAULT_SLOTS = 2048;
    private static final char DEFAULT_GROWTH = EXPONENTIAL_GROWTH;
    
    public SuccessorVirtualMachine() throws InternalException {
        this(DEFAULT_SEGMENTS, DEFAULT_SLOTS, DEFAULT_GROWTH);
    }
    
    public SuccessorVirtualMachine(int segments, int slots,
                                   char growth) throws InternalException {
        checkInitialConditions(segments, slots, growth);
        numSegments = segments;
        initialSlots = slots;
        growthRate = growth;
        intHeapSegments = new int[numSegments][];
        doubleHeapSegments = new double[numSegments][];
        stringHeapSegments = new String[numSegments][];
        mintObjHeapSegments = new MintObject[numSegments][];
        intHeapSegments[0] = new int[initialSlots];
        doubleHeapSegments[0] = new double[initialSlots];
        stringHeapSegments[0] = new String[initialSlots];
        mintObjHeapSegments[0] = new MintObject[initialSlots];
    }
    
    public void writeInt(int i, int address) {
        
    }
    
    public void writeDouble(double d, int address) {
        
    }
    
    public void writeString(String s, int address) {
        
    }
    
    public void writeMintObj(MintObject m, int address) {
        
    }
    
    /**
     * Given a address, returns the segment index of that address.
     * @param address
     * @return 
     */
    public int getEffectiveSegment(int address) {
        switch (growthRate) {
            case LINEAR_GROWTH:
                return address / initialSlots;
            case QUADRATIC_GROWTH:
                return (int) Math.sqrt(address / initialSlots);
            case EXPONENTIAL_GROWTH:
                return log2OfPowerOf2(address / initialSlots);
            default:
                return -1;
        }
    }
    
    /**
     * Returns log base 2 of the argument, which must be a power of 2.
     * When given 0, returns 0.
     * @param powerOf2
     * @return log base 2 of the argument, only if the argument is a power of 2.
     */
    public static int log2OfPowerOf2(int powerOf2) {
        for (int i = 0; i < 32; i++) {
            if ((powerOf2 & 1) == 1) {
                return i;
            }
            powerOf2 >>= 1;
        }
        return 31;
    }
    
    /**
     * 
     * @param segments
     * @param slots
     * @param growth
     * @return maximum heap size under these conditions
     * @throws InternalException if heap would be too large given conditions
     */
    private static int checkInitialConditions(int segments, int slots,
            char growth) throws InternalException {
        int maxHeapSize;
        if (segments <= 0) {
            throw new InternalException("Number of segments is negative or 0.");
        } else if (slots <= 0) {
            throw new InternalException("Initial slots is negative or 0.");
        } else {
            switch (growth) {
                case LINEAR_GROWTH:
                    if (multiplyOverflows(segments, slots)) {
                        throw new InternalException(
                            "Linear growth rate will overflow.");
                    }
                    maxHeapSize = segments * slots;
                    break;
                case QUADRATIC_GROWTH:
                    if (multiplyOverflows(segments * segments,
                                          slots)) {
                        throw new InternalException(
                            "Quadratic growth rate will overflow.");
                    }
                    maxHeapSize = segments * segments * slots;
                    break;
                case EXPONENTIAL_GROWTH:
                    if (multiplyOverflows(1 << segments,
                                          slots)) {
                        throw new InternalException(
                            "Exponential growth rate will overflow.");
                    }
                    maxHeapSize = (1 << segments) * slots;
                    break;
                default:
                    throw new InternalException("Illegal growth rate.");
            }
        }
        return maxHeapSize;
    }
    
    public static String reportMaximumHeapSize(char growth, int slots) {
        int size = -1;
        int previousSize = -1;
        if (slots == 0) {
            return "Not useful to have 0 slots per segment.";
        }
        for (int segments = 1; segments <= Integer.MAX_VALUE; segments++) {
            try {
                size = checkInitialConditions(segments, slots, growth);
            } catch (InternalException ex) {
                return "Max segments: " + (segments - 1) + ", Max size: " +
                       previousSize + " bytes";
            }
            previousSize = size;
        }
        return "Max segments: " + Integer.MAX_VALUE + ", Max size: " +
               Integer.MAX_VALUE;
    }
    
    private static boolean multiplyOverflows(int a, int b) {
        if (a == 0) {
            return false;
        } else {
            return b > Integer.MAX_VALUE / a;
        }
    }
}
