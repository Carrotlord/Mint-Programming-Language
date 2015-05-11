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
            }
        };
        setSubtests(tests);
    }
    
    @Override
    protected boolean mainTest() {
        return true;
    }
}
