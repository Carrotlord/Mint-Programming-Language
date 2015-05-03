package mint;

import java.util.regex.Pattern;

/**
 * Assembles Successor assembly language mnemonics into executable bytecode.
 * @author Jiangcheng Oliver Chu
 */
public class Assembler {
    private static final Pattern TYPE_PATTERN = Pattern.compile("^[ifso]$");
    private static final Pattern MNEMONIC_PATTERN = Pattern.compile("^[a-z]+$");
    private static final Pattern REGISTER_PATTERN = Pattern.compile(
                                                 "^r(\\d|[A-Z])+$");
    private static final Pattern CONSTANT_PATTERN = Pattern.compile(
                                                 "^-?(\\d+|0x\\d+)$");
    public static final String SUCCESSOR_NOP = "i shlv rZERO, rZERO, 0\n";
    public static final String SUCCESSOR_EXIT = "i syscall r0, rZERO, 0";
    private String[] sourceCodeLines;
    private int[][] programByteCode;
    
    public Assembler(String code) {
        sourceCodeLines = code.split("\\n");
        programByteCode = null;
    }
    
    public int[][] compile() throws InternalException {
        if (programByteCode != null) {
            return programByteCode;
        } else {
            programByteCode = new int[sourceCodeLines.length][2];
            for (int i = 0; i < sourceCodeLines.length; i++) {
                String line = sourceCodeLines[i];
                programByteCode[i] = assembleParts(getParts(line));
            }
            return programByteCode;
        }
    }
    
    public static int[][] compileDirectly(String code)
           throws InternalException {
        return new Assembler(code).compile();
    }
    
    public static int[] flatCompileDirectly(String code)
           throws InternalException {
        int[][] compiledProgram = compileDirectly(code);
        int[] flattenedProgram = new int[2 * compiledProgram.length];
        for (int i = 0, j = 0; i < compiledProgram.length; i++, j += 2) {
            flattenedProgram[j] = compiledProgram[i][0];
            flattenedProgram[j + 1] = compiledProgram[i][1];
        }
        return flattenedProgram;
    }
    
    public static int getOpcode(String mnemonic) throws InternalException {
        /* Warning: String switches require JDK 1.7 */
        switch (mnemonic) {
            case "shlv":
                return 0x0;
            case "shrv":
                return 0x1;
            case "sharv":
                return 0x2;
            case "mov":
                return 0x3;
            case "add":
                return 0x4;
            case "sub":
                return 0x5;
            case "mul":
                return 0x6;
            case "div":
                return 0x7;
            case "mod":
                return 0x8;
            case "and":
                return 0x9;
            case "or":
                return 0xa;
            case "xor":
                return 0xb;
            case "j":
                return 0xc;
            case "jmp":
                return 0xd;
            case "jeq":
                return 0xe;
            case "jne":
                return 0xf;
            case "jge":
                return 0x10;
            case "jg":
                return 0x11;
            case "jle":
                return 0x12;
            case "jl":
                return 0x13;
            case "call":
                return 0x14;
            case "syscall":
                return 0x15;
            case "ret":
                return 0x16;
            case "load":
                return 0x17;
            case "save":
                return 0x18;
            case "push":
                return 0x19;
            case "pop":
                return 0x1a;
            default:
                throw new InternalException("Unknown mnemonic " + mnemonic);
        }
    }
    
    public static int getTypeCode(char type) throws InternalException {
        switch (type) {
            case 'i':
            case InstructionParts.NO_TYPE:
                return 0x0;
            case 'f':
                return 0x1;
            case 's':
                return 0x2;
            case 'o':
                return 0x3;
            default:
                throw new InternalException("Unknown static type: " + type +
                                            " (" + (int) type + ")");
        }
    }
    
    public static int compileRegister(String register)
        throws InternalException {
        if (register.length() == 0 || register.charAt(0) != 'r') {
            throw new InternalException(
                "Register name does not start with 'r': " + register);
        }
        register = register.substring(1);
        /* Warning: String switches require JDK 1.7 */
        switch (register) {
            case "ZERO":
                return 0;
            case "AT":
                return 60;
            case "GP":
                return 61;
            case "SP":
                return 62;
            case "BP":
                return 63;
            default:
                int regNumber = -1;
                try {
                    regNumber = Integer.parseInt(register);
                } catch (NumberFormatException ex) {
                    throw illegalRegister(register);
                }
                if (regNumber < 0 || regNumber > 63) {
                    throw illegalRegister(register);
                }
                return regNumber;
        }
    }
    
    private static InternalException illegalRegister(String register) {
        return new InternalException(
                       "Numbered registers are only r0 through r63, saw r" +
                        register);
    }
    
    private InstructionParts getParts(String instruction) {
        String[] pieces = instruction.replace(",", " ").split(" ");
        InstructionParts result = new InstructionParts(instruction);
        for (String piece : pieces) {
            if (piece.length() == 1 && TYPE_PATTERN.matcher(piece).find()
                && result.getType() == InstructionParts.NO_TYPE) {
                result.setType(piece.charAt(0));
            } else if (MNEMONIC_PATTERN.matcher(piece).find() &&
                       result.getMnemonic() == null) {
                result.setMnemonic(piece);
            } else if (CONSTANT_PATTERN.matcher(piece).find() &&
                       result.getConstant() == null) {
                result.setConstant(piece);
            } else if (REGISTER_PATTERN.matcher(piece).find()) {
                if (result.getRegA() == null) {
                    result.setRegA(piece);
                } else if (result.getRegB() == null) {
                    result.setRegB(piece);
                } else if (result.getRegC() == null) {
                    result.setRegC(piece);
                }
            }
        }
        return result;
    }

    private class InstructionParts {
        private char type;
        private String mnemonic;
        private String rA;
        private String rB;
        private String rC;
        private String constant;
        private String originalInstruction;
        public static final char NO_TYPE = (char) -1;

        public InstructionParts(String instruction) {
            type = NO_TYPE;
            mnemonic = null;
            rA = null;
            rB = null;
            rC = null;
            constant = null;
            originalInstruction = instruction;
        }
        
        /**
         * Getter for original instruction. Only useful for debugging.
         * @return the string instruction from which this object is derived
         */
        public String getOriginalInstruction() {
            return originalInstruction;
        }
        
        public void setType(char t) {
            type = t;
        }
        
        public void setMnemonic(String m) {
            mnemonic = m;
        }
        
        public void setRegA(String regA) {
            rA = regA;
        }
        
        public void setRegB(String regB) {
            rB = regB;
        }
        
        public void setRegC(String regC) {
            rC = regC;
        }
        
        public void setConstant(String c) {
            constant = c;
        }
        
        public char getType() {
            return type;
        }
        
        public String getMnemonic() {
            return mnemonic;
        }
        
        public String getRegA() {
            return rA;
        }
        
        public String getRegB() {
            return rB;
        }
        
        public String getRegC() {
            return rC;
        }
        
        public String getConstant() {
            return constant;
        }
        
        @Override
        public String toString() {
            return "Instruction<" + type + " " + mnemonic + " " + rA + ", " +
                   rB + ", " + rC + ", " + constant + ">";
        }
    }
    
    private int[] assembleParts(InstructionParts parts)
            throws InternalException {
        int firstFields = 0;
        if (parts.getMnemonic().equals("j")) {
            firstFields = 0x600000;
        } else if (parts.getMnemonic().equals("call")) {
            firstFields = 0xa00000;
        } else if (parts.getMnemonic().equals("ret")) {
            firstFields = 0xb00000;
        } else {
            char type = parts.getType();
            String mnemonic = parts.getMnemonic();
            String regA = parts.getRegA();
            String regB = parts.getRegB();
            if (type == -1 || mnemonic == null || regA == null ||
                regB == null) {
                throw new InternalException(
                    "Successor syntax error on instruction: " +
                    parts.getOriginalInstruction() + " -> " + parts);
            } else {
                firstFields = (getTypeCode(type) << 30) |
                              (getOpcode(mnemonic) << 19) |
                              (compileRegister(regA) << 6) |
                              (compileRegister(regB));
            }
        }
        int lastFields;
        String constant = parts.getConstant();
        if (constant == null) {
            lastFields = 0;
        } else {
            try {
                lastFields = Integer.parseInt(constant);
            } catch (NumberFormatException ex) {
                throw new InternalException("Illegal successor constant " +
                                            constant);
            }
        }
        return new int[]{firstFields, lastFields};
    }
}
