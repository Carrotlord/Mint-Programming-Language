package mint;

import java.util.NoSuchElementException;

/**
 * A linked-list of chars that avoids taking up large amounts of memory.
 * Intended as a stack and a queue.
 * @author Jiangcheng Oliver Chu
 */
public class CharLinkedList {
    private CharNodeBundle eldest = null;
    private CharNodeBundle youngest = null;
    public static final char NIL = 0;
    private int writePosition = 0;
    private int readPosition = 0;
    
    public CharLinkedList() {}
    
    public void push(char c) {
        if (eldest == null || youngest == null) {
            eldest = new CharNodeBundle();
            youngest = eldest;
            writeToCurrent(c);
        } else {
            if (youngest.isFull()) {
                CharNodeBundle newChild = new CharNodeBundle();
                connect(youngest, newChild);
                youngest = newChild;
                writeToCurrent(c);
            } else {
                writeToCurrent(c);
            }
        }
    }
    
    private void connect(CharNodeBundle parent, CharNodeBundle child) {
        parent.setChild(child);
        child.setParent(parent);
    }
    
    public char poll() throws NoSuchElementException {
        char result = NIL;
        if (eldest == null) {
            throw new NoSuchElementException(
                "No elements ever added to this char queue.");
        }
        while (result == NIL) {
            if (readPosition > writePosition) {
                throw new NoSuchElementException(
                          "Char queue has no more elements.");
            }
            result = eldest.read(readPosition & 0x3);
            checkDepletion();
        }
        return result;
    }
    
    public char pop() throws NoSuchElementException {
        char result = NIL;
        if (eldest == null) {
            throw new NoSuchElementException(
                "No elements ever added to this char stack.");
        }
        if (youngest == null) {
            throw new NoSuchElementException(
                "No elements left in the stack.");
        }
        while (result == NIL) {
            writePosition--;
            if (writePosition < 0) {
                throw new NoSuchElementException(
                          "Char stack has no more elements.");
            }
            result = youngest.skim(writePosition & 0x3);
            checkEmptyStack();
        }
        return result;
    }
    
    private void checkEmptyStack() {
        if (youngest.isComplementary()) {
            youngest = youngest.getParent();
        }
    }
    
    private void checkDepletion() {
        if (eldest.isDepleted()) {
            eldest = eldest.getChild();
        }
        readPosition++;
    }
    
    private void writeToCurrent(char c) {
        youngest.write(c, writePosition & 0x3);
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
        return "CharLinkedList[\"" + result + "\"]";
    }
    
    /**
     * Returns a string representation of this, without mutating this.
     * @return 
     */
    @Override
    public String toString() {
        String result = "";
        CharNodeBundle originalFollower = eldest;
        while (eldest != null) {
            result += eldest.toString();
            eldest = eldest.getChild();
        }
        eldest = originalFollower;
        return "CharLinkedList[\"" + result + "\"]";
    }
    
    public String convertToReversedString() {
        String result = "";
        for (;;) {
            try {
                result += pop();
            } catch (NoSuchElementException ex) {
                break;
            }
        }
        return "CharLinkedList[\"" + result + "\"]";
    }
    
    private class CharNodeBundle {
        private long contents = 0;
        private byte writtenTo = 0;
        private byte readFrom = 0;
        private static final long MASK = 0xffff;
        private CharNodeBundle child;
        private CharNodeBundle parent;
        
        CharNodeBundle(CharNodeBundle rest) {
            child = rest;
            parent = null;
        }
        
        CharNodeBundle() {
            this(null);
        }
        
        CharNodeBundle getChild() {
            return child;
        }
        
        void setChild(CharNodeBundle rest) {
            child = rest;
        }
        
        void setParent(CharNodeBundle ancestor) {
            parent = ancestor;
        }
        
        CharNodeBundle getParent() {
            return parent;
        }
        
        boolean isFull() {
            return writtenTo == (byte) 0xf;
        }
        
        boolean isDepleted() {
            return readFrom == (byte) 0xf;
        }
        
        /**
         * Returns true iff the bits read are exactly those that
         * have been written.
         * @return true iff all reads complement all writes
         */
        boolean isComplementary() {
            return (readFrom ^ writtenTo) == (byte) 0;
        }
        
        void write(char c, int position) {
            int index = position << 4;
            contents &= ~(MASK << index);
            contents |= ((long) c) << index;
            writtenTo |= 1 << position;
        }
        
        char read(int position) {
            int index = position << 4;
            readFrom |= 1 << position;
            return (char) ((contents & (MASK << index)) >>> index);
        }

        char skim(int position) {
            int index = position << 4;
            char result = (char) ((contents & (MASK << index)) >>> index);
            if (result == NIL) {
                return NIL;
            } else {
                readFrom |= 1 << position;
                return result;
            }
        }

        @Override
        public String toString() {
            String result = "";
            for (int i = 0; i < 4; i++) {
                long position = i << 4;
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
