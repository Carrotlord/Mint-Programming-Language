package test;

import java.util.Arrays;
import mint.Assembler;
import mint.InternalException;

/**
 *
 * @author Jiangcheng Oliver Chu
 */
public class AssemblerTests extends TestGroup {
    public AssemblerTests() {
        super("assembler_tests");
        TestGroup[] tests = {
            new TestGroup("mnemonic_get_opcodes") {
                @Override
                protected boolean mainTest() {
                    try {
                        return Assembler.getOpcode("mov") == 0x3 &&
                               Assembler.getOpcode("add") == 0x4;
                    } catch (InternalException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("compiled_for_loop") {
                @Override
                protected boolean mainTest() {
                    String forLoopProgram = Assembler.SUCCESSOR_NOP +
                                            "i mov r2, rZERO, 0\n" +
                                            "i mov r1, rZERO, 0\n" +
                                            "i mov r6, rZERO, 9\n" +
                                            "jge r2, r6, 8\n" +
                                            "i add r1, rZERO, 100\n" +
                                            "i add r2, rZERO, 1\n" +
                                            "j 4\n" +
                                            "j -1";
                    int[] correctCompilation = {
                        0x0, 0x0,
                        0x180080, 0x0,
                        0x180040, 0x0,
                        0x180180, 0x9,
                        0x800086, 0x8,
                        0x200040, 0x64,
                        0x200080, 0x1,
                        0x600000, 0x4,
                        0x600000, -0x1
                    };
                    try {
                        int[] compiledProgram = Assembler.flatCompileDirectly(
                                                forLoopProgram);
                        if (!Arrays.equals(correctCompilation,
                            compiledProgram)) {
                            setFailureMessage(
                                TestTools.arrayEqualsGreedyFailure(
                                TestTools.boxHexIntArray(correctCompilation),
                                TestTools.boxHexIntArray(compiledProgram)));
                            return false;
                        }
                        return true;
                    } catch (InternalException ex) {
                        setFailureMessage(ex.toString());
                        return false;
                    }
                }
            },
            new TestGroup("compiled_recursive_factorial") {
                @Override
                protected boolean mainTest() {
                    String recursiveFactorialProgram =
                        Assembler.SUCCESSOR_NOP +
                        "i mov r1, rZERO, 6\n" +
                        "i sub rSP, rZERO, 1\n" +
                        "i save r1, rSP, rZERO, 0\n" +
                        "call 7\n" +
                        "j -1\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i sub rSP, rZERO, 1\n" +
                        "i save rBP, rSP, rZERO, 0\n" +
                        "i mov rBP, rSP, 0\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i load r1, rBP, rZERO, 2\n" +
                        "jeq r1, rZERO, 20\n" +
                        "i sub r1, rZERO, 1\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i sub rSP, rZERO, 1\n" +
                        "i save r1, rSP, rZERO, 0\n" +
                        "call 7\n" +
                        "i add rSP, rZERO, 1\n" +
                        "j 22\n" +
                        "i mov r5, rZERO, 1\n" +
                        "j 26\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i load r6, rBP, rZERO, 2\n" +
                        "i mul r5, r6, 0\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i load rBP, rSP, rZERO, 0\n" +
                        "add rSP, rZERO, 1\n" +
                        "ret";
                    int[] correctCompilation = {
                        0x0, 0x0,
                        0x180040, 0x6,
                        0x280f80, 0x1,
                        0xc0007e, 0x0,
                        0xa00000, 0x7,
                        0x600000, -0x1,
                        0x0, 0x0,
                        0x280f80, 0x1,
                        0xc00ffe, 0x0,
                        0x180ffe, 0x0,
                        0x0, 0x0,
                        0xb8007f, 0x2,
                        0x700040, 0x14,
                        0x280040, 0x1,
                        0x0, 0x0,
                        0x280f80, 0x1,
                        0xc0007e, 0x0,
                        0xa00000, 0x7,
                        0x200f80, 0x1,
                        0x600000, 0x16,
                        0x180140, 0x1,
                        0x600000, 0x1a,
                        0x0, 0x0,
                        0xb801bf, 0x2,
                        0x300146, 0x0,
                        0x0, 0x0,
                        0xb80ffe, 0x0,
                        0x200f80, 0x1,
                        0xb00000, 0x0
                    };
                    try {
                        int[] compiledProgram = Assembler.flatCompileDirectly(
                                                recursiveFactorialProgram);
                        if (!Arrays.equals(correctCompilation,
                            compiledProgram)) {
                            setFailureMessage(
                                TestTools.arrayEqualsGreedyFailure(
                                TestTools.boxHexIntArray(correctCompilation),
                                TestTools.boxHexIntArray(compiledProgram)));
                            return false;
                        }
                        return true;
                    } catch (InternalException ex) {
                        setFailureMessage(ex.toString());
                        return false;
                    }
                }
            }
        };
        setSubtests(tests);
    }
    
    @Override
    protected boolean mainTest() {
        return true;
    }
}
