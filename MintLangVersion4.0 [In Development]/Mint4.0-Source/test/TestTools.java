package test;

import mint.InternalException;

/**
 *
 * @author Jiangcheng Oliver Chu
 */
public class TestTools {   
    public static <E> String arrayEqualsGreedyFailure(E[] array,
                                                      E[] otherArray) {
        boolean isFirstArrayNull = array == null;
        boolean isSecondArrayNull = otherArray == null;
        if (isFirstArrayNull) {
            if (isSecondArrayNull) {
                return "Both arrays are null";
            }
            return "First array is null";
        } else if (isSecondArrayNull) {
            return "Second array is null";
        }
        if (array.length != otherArray.length) {
            return "Arrays have differing lengths: " + array.length +
                   ", " + otherArray.length;
        }
        boolean doesDiffer = false;
        String message = "Arrays differ at the following indices: ";
        for (int i = 0; i < array.length; i++) {
            E element = array[i];
            if (!element.equals(otherArray[i])) {
                message += i + " (" + element + " vs " + otherArray[i] + "), ";
                doesDiffer = true;
            }
        }
        message = message.substring(0, message.length() - 2);
        if (!doesDiffer) {
            return "Arrays are actually equal, no error!";
        }
        return message;
    }
    
    public static void assertTrue(boolean condition) throws InternalException {
        if (!condition) {
            throw new InternalException("Assertion is false.");
        }
    }
    
    public static Integer[] boxIntArray(int[] array) {
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Integer(array[i]);
        }
        return result;
    }
    
    public static HexInteger[] boxHexIntArray(int[] array) {
        HexInteger[] result = new HexInteger[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new HexInteger(array[i]);
        }
        return result;
    }

    private static class HexInteger extends Number {
        private int contents;
        
        public HexInteger(int i) {
            contents = i;
        }

        @Override
        public int intValue() {
            return contents;
        }

        @Override
        public long longValue() {
            return (long) contents;
        }

        @Override
        public float floatValue() {
            return (float) contents;
        }

        @Override
        public double doubleValue() {
            return (double) contents;
        }
        
        @Override
        public String toString() {
            return "0x" + Integer.toHexString(contents);
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof Number) {
                Number n = (Number) o;
                return n.intValue() == intValue();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return contents;
        }
    }
}
