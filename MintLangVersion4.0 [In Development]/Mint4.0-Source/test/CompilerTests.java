package test;

import mint.MintException;
import mint.ShuntingYard;

/**
 * Tests for the Mint compiler.
 * @author Jiangcheng Oliver Chu
 */
public class CompilerTests extends TestGroup {
    public CompilerTests() {
        super("compiler_tests");
        TestGroup[] tests = {
            new TestGroup("shunting_yard") {
                @Override
                protected boolean mainTest() {
                    String expression = "3+4*5";
                    try {
                        String rpn =
                          ShuntingYard.infixToRPN(expression).convertToString();
                        return rpn.equals("CharLinkedList[\"345*+\"]");
                    } catch (MintException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("shunting_yard2") {
                @Override
                protected boolean mainTest() {
                    String expression = "3*x^5+8*x^2+a:b";
                    try {
                        String rpn =
                          ShuntingYard.infixToRPN(expression).convertToString();
                        return rpn.equals(
                               "CharLinkedList[\"3x5^*8x2^*ab:++\"]");
                    } catch (MintException ex) {
                        return false;
                    }
                }
            },
            new TestGroup("shunting_yard3") {
                @Override
                protected boolean mainTest() {
                    String expression = "(3+4)*(5+(8-y):(3^(5/x)+x))";
                    try {
                        String rpn =
                          ShuntingYard.infixToRPN(expression).convertToString();
                        return rpn.equals(
                               "CharLinkedList[\"34+58y-35x/^x+:+*\"]");
                    } catch (MintException ex) {
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
