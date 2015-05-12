package test;

import mint.Assembler;
import mint.InternalException;
import mint.Mint;
import mint.SuccessorVirtualMachine;

/**
 *
 * @author Jiangcheng Oliver Chu
 */
public class SuccessorTests extends TestGroup {
    public SuccessorTests() {
        super("successor_vm_tests");
        TestGroup[] tests = {
            new TestGroup("register_machine_test") {
                @Override
                protected boolean mainTest() {
                    String assignAndPrint = "i mov r1, rZERO, 99999\n" +
                                            "i syscall r3, r1, 0";
                    try {
                        int[] compiled =
                              Assembler.flatCompileDirectly(assignAndPrint);
                        SuccessorVirtualMachine vm =
                            new SuccessorVirtualMachine(compiled);
                        int exitCode = vm.execute();
                        return exitCode == SuccessorVirtualMachine.EXIT_EOF;
                    } catch (InternalException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("register_machine_count_0_to_10") {
                @Override
                protected boolean mainTest() {
                    String forLoop = "i mov r1, rZERO, 0\n" +
                                     "i mov r2, rZERO, 10\n" +
                                     "i syscall r3, r1, 0\n" +
                                     "i add r1, rZERO, 1\n" +
                                     "i jle r1, r2, 2\n" +
                                     Assembler.SUCCESSOR_EXIT;
                    try {
                        int[] compiled =
                              Assembler.flatCompileDirectly(forLoop);
                        SuccessorVirtualMachine vm =
                            new SuccessorVirtualMachine(compiled);
                        int exitCode = vm.execute();
                        return exitCode == SuccessorVirtualMachine.EXIT_SUCCESS;
                    } catch (InternalException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("simple_for_loop") {
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
                                            "i syscall r3, r1, 0\n" +
                                            Assembler.SUCCESSOR_EXIT;
                    try {
                        int[] compiled =
                              Assembler.flatCompileDirectly(forLoopProgram);
                        SuccessorVirtualMachine vm =
                            new SuccessorVirtualMachine(compiled);
                        int exitCode = vm.execute();
                        return exitCode == SuccessorVirtualMachine.EXIT_SUCCESS;
                    } catch (InternalException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("function_calls") {
                @Override
                protected boolean mainTest() {
                    String functionProgram = "j 3\n" +
                                             "i mul r7, rZERO, 3\n" +
                                             "ret\n" +
                                             "i mov r7, rZERO, 10\n" +
                                             "i mov r1, rZERO, 4\n" +
                                             "call 1\n" +
                                             "i sub r1, rZERO, 1\n" +
                                             "jg r1, rZERO, 5\n" +
                                             "i syscall r3, r7, 0\n" +
                                             Assembler.SUCCESSOR_EXIT;
                    try {
                        int[] compiled =
                              Assembler.flatCompileDirectly(functionProgram);
                        SuccessorVirtualMachine vm =
                            new SuccessorVirtualMachine(compiled);
                        int exitCode = vm.execute();
                        return exitCode == SuccessorVirtualMachine.EXIT_SUCCESS;
                    } catch (InternalException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("recursive_factorial") {
                @Override
                protected boolean mainTest() {
                    String recursiveFactorial =
                        Assembler.SUCCESSOR_NOP +
                        "i mov r1, rZERO, 6\n" +
                        "i sub rSP, rZERO, 1\n" +
                        "i save r1, rSP, rZERO, 0\n" +
                        "call 7\n" +
                        "i syscall r3, r5, 0\n" +
                        Assembler.SUCCESSOR_EXIT + "\n" +
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
                        "i add rSP, rZERO, 1\n" +
                        "ret";
                    try {
                        int[] compiled =
                              Assembler.flatCompileDirectly(recursiveFactorial);
                        SuccessorVirtualMachine vm =
                            new SuccessorVirtualMachine(compiled);
                        int exitCode = vm.execute();
                        return exitCode == SuccessorVirtualMachine.EXIT_SUCCESS;
                    } catch (InternalException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("recursive_fibonacci") {
                @Override
                protected boolean mainTest() {
                    String recursiveFib =
                        Assembler.SUCCESSOR_NOP +
                        "i mov r1, rZERO, 33\n" +
                        "i sub rSP, rZERO, 1\n" +
                        "i save r1, rSP, rZERO, 0\n" +
                        "call 8\n" +
                        "i add rSP, rZERO, 1\n" +
                        "i syscall r3, r5, 0\n" +
                        Assembler.SUCCESSOR_EXIT + "\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i sub rSP, rZERO, 1\n" +
                        "i save rBP, rSP, rZERO, 0\n" +
                        "i mov rBP, rSP, 0\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i load r1, rBP, rZERO, 2\n" +
                        "i mov r9, rZERO, 1\n" +
                        "jle r1, r9, 40\n" +
                        "i sub r1, rZERO, 1\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i sub rSP, rZERO, 1\n" +
                        "i save r1, rSP, rZERO, 0\n" +
                        "call 8\n" +
                        "i add rSP, rZERO, 1\n" +
                        "i mov r6, r5, 0\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i load r1, rBP, rZERO, 2\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i sub rSP, rZERO, 1\n" +
                        "i save r6, rSP, rZERO, 0\n" +
                        "i sub r1, rZERO, 2\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i sub rSP, rZERO, 1\n" +
                        "i save r1, rSP, rZERO, 0\n" +
                        "call 8\n" +
                        "i add rSP, rZERO, 1\n" +
                        Assembler.SUCCESSOR_NOP +
                        Assembler.SUCCESSOR_NOP +
                        "i load r6, rSP, rZERO, 0\n" +
                        "i add rSP, rZERO, 1\n" +
                        "i add r5, r6, 0\n" +
                        "j 42\n" +
                        "i mov r5, r1, 0\n" +
                        Assembler.SUCCESSOR_NOP +
                        "i load rBP, rSP, rZERO, 0\n" +
                        "add rSP, rZERO, 1\n" +
                        "ret";
                    try {
                        int[] compiled =
                              Assembler.flatCompileDirectly(recursiveFib);
                        SuccessorVirtualMachine vm =
                            new SuccessorVirtualMachine(compiled);
                        int exitCode = vm.execute();
                        return exitCode == SuccessorVirtualMachine.EXIT_SUCCESS;
                    } catch (InternalException ex) {
                        return false;
                    }
                }
            }
        };
        setSubtests(tests);
    }
    
    @Override
    protected boolean mainTest() {
        TestGroup fibonacciTest = findTest("recursive_fibonacci");
        if (fibonacciTest == null) {
            return false;
        } else {
            Mint.manager.debugln("Fibonacci takes: " +
                                 fibonacciTest.getTimeTaken() + " seconds");
        }
        return true;
    }
}
