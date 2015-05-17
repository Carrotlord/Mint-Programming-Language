package mint;

import java.util.LinkedList;

/**
 * Compiles reverse polish notation into Successor instructions.
 * @author Jiangcheng Oliver Chu
 */
public class RPNCompiler {
    private DenseLinkedList stack = new DenseLinkedList();
    private String code;
    private int currentTempVar = 0;
    private LinkedList<String> successorOutput = new LinkedList<String>();
    
    public RPNCompiler(String rpnProgram) {
        code = rpnProgram;
    }
    
    private String readOutput() {
        String output = "";
        while (!successorOutput.isEmpty()) {
            output += successorOutput.pollLast();
        }
        return output;
    }
    
    private int currentAddress(int offset) {
        return successorOutput.size() + offset;
    }
    
    private int currentAddress() {
        return currentAddress(0);
    }
    
    private String getNewTempVar() {
        currentTempVar++;
        return "t" + (currentTempVar - 1);
    }
    
    private char collapse(String name) {
        short bits = (short) (((byte) name.charAt(0)) << 8);
        bits |= ((byte) name.charAt(1));
        return (char) bits;
    }
    
    private String expand(char collapsed) {
        char highBits = (char) (collapsed >>> 8);
        if (highBits == 't') {
            return highBits + "" + (char) (collapsed & 0xff);
        } else {
            return "" + collapsed;
        }
    }
    
    private void binaryOperationToOutput(String mnemonic) {
        char first = stack.pop();
        char second = stack.pop();
        String tempVar = getNewTempVar();
        successorOutput.push(
            "i mov " + tempVar + "," + expand(first) + ",0\n");
        successorOutput.push(
            "i " + mnemonic + " " + tempVar + "," + expand(second) + ",0\n");
        stack.push(collapse(tempVar));
    }
    
    private void compareOperationToOutput(String mnemonic) {
        char first = stack.pop();
        char second = stack.pop();
        String tempVar = getNewTempVar();
        successorOutput.push("i mov " + tempVar + ",r0,0\n");
        successorOutput.push(
            "i " + mnemonic + " " + expand(first) + "," +
            expand(second) + "," + currentAddress(2) + "\n"
        );
        successorOutput.push("i mov " + tempVar + ",r0,1\n");
        stack.push(collapse(tempVar));
    }
    
    public String compile() {
        for (char instruction : code.toCharArray()) {
            if ((instruction >= '0' && instruction <= '9') ||
                (instruction >= 'a' && instruction <= 'z') ||
                (instruction >= 'A' && instruction <= 'Z')) {
                stack.push(instruction);
            } else {
                switch (instruction) {
                    case ShuntingYard.ADD:
                        binaryOperationToOutput("add");
                        break;
                    case ShuntingYard.MULTIPLY:
                        binaryOperationToOutput("mul");
                        break;
                    case ShuntingYard.SUBTRACT:
                        binaryOperationToOutput("sub");
                        break;
                    case ShuntingYard.REMAINDER:
                        binaryOperationToOutput("mod");
                        break;
                    case ShuntingYard.COMPARE_EQUAL:
                        compareOperationToOutput("jeq");
                        break;
                    case ShuntingYard.NOT_EQUAL:
                        compareOperationToOutput("jne");
                        break;
                    case ShuntingYard.GREATER:
                        compareOperationToOutput("jge");
                        break;
                    case ShuntingYard.LESSER:
                        compareOperationToOutput("jle");
                        break;
                    case ShuntingYard.GREATER_EQUAL:
                        compareOperationToOutput("jg");
                        break;
                    case ShuntingYard.LESSER_EQUAL:
                        compareOperationToOutput("jl");
                        break;
                    default:
                        break;
                }
            }
        }
        return readOutput();
    }
}
