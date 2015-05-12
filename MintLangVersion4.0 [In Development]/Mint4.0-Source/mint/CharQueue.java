package mint;

import java.util.NoSuchElementException;

/**
 * A queue of chars that avoids taking up large amounts of memory.
 * @author Jiangcheng Oliver Chu
 */
public class CharQueue {
    private CharNodeBundle follower = null;
    private CharNodeBundle leader = null;
    public static final char NIL = 0;
    private int writePosition = 0;
    private int readPosition = 0;
    
    public CharQueue() {}
    
    public void push(char c) {
        if (follower == null) {
            follower = new CharNodeBundle();
            leader = follower;
            writeToCurrent(c);
        } else {
            if (leader.isFull()) {
                CharNodeBundle newTail = new CharNodeBundle();
                leader.setTail(newTail);
                leader = newTail;
                writeToCurrent(c);
            } else {
                writeToCurrent(c);
            }
        }
    }
    
    public char poll() throws NoSuchElementException {
        char result = NIL;
        while (result == NIL) {
            if (follower == null) {
                throw new NoSuchElementException(
                    "No elements ever added to this char queue.");
            }
            result = follower.read(readPosition & 0x7);
            if (readPosition > writePosition) {
                throw new NoSuchElementException(
                          "Char queue has no more elements.");
            }
            checkDepletion();
        }
        return result;
    }
    
    private void checkDepletion() {
        if (follower.isDepleted()) {
            follower = follower.getTail();
        }
        readPosition++;
    }
    
    private void writeToCurrent(char c) {
        leader.write(c, writePosition & 0x7);
        writePosition++;
    }
    
    /**
     * Destroys this queue and returns a string representation of it,
     * ignoring NIL characters.
     * @return this, as a string
     */
    public String convertToString() {
        String result = "";
        for (;;) {
            try {
                result += poll();
            } catch (NoSuchElementException ex) {
                break;
            }
        }
        return "CharQueue[\"" + result + "\"]";
    }
    
    /**
     * Returns a string representation of this, without mutating this.
     * @return 
     */
    public String toString() {
        String result = "";
        CharNodeBundle originalFollower = follower;
        while (follower != null) {
            result += follower.toString();
            follower = follower.getTail();
        }
        follower = originalFollower;
        return "CharQueue[\"" + result + "\"]";
    }
    
    private class CharNodeBundle {
        private long contents = 0;
        private byte writtenTo = 0;
        private byte readFrom = 0;
        private static final long MASK = 0xff;
        private CharNodeBundle tail;
        
        CharNodeBundle(CharNodeBundle rest) {
            tail = rest;
        }
        
        CharNodeBundle() {
            this(null);
        }
        
        CharNodeBundle getTail() {
            return tail;
        }
        
        void setTail(CharNodeBundle rest) {
            tail = rest;
        }
        
        boolean isFull() {
            return writtenTo == (byte) 0xff;
        }
        
        boolean isDepleted() {
            return readFrom == (byte) 0xff;
        }
        
        void write(char c, int position) {
            int index = position << 3;
            contents &= ~(MASK << index);
            contents |= ((long) c) << index;
            writtenTo |= 1L << position;
        }
        
        char read(int position) {
            int index = position << 3;
            readFrom |= 1 << position;
            return (char) ((contents & (MASK << index)) >>> index);
        }
        
        @Override
        public String toString() {
            String result = "";
            for (int i = 0; i < 8; i++) {
                long position = i << 3;
                char appendable =
                     (char) ((contents & (MASK << position)) >>> position);
                if (appendable == NIL) {
                    result += "\\0";
                } else {
                    result += appendable;
                }
            }
            return result;
        }
    }
}
