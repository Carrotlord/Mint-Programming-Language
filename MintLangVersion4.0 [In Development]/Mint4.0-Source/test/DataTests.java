package test;

import mint.DenseLinkedList;

/**
 * Tests for custom data structures.
 * @author Jiangcheng Oliver Chu
 */
public class DataTests extends TestGroup {
    public DataTests() {
        super("data_tests");
        TestGroup[] tests = {
            new TestGroup("charqueue") {
                @Override
                protected boolean mainTest() {
                    String str = "This is a test.";
                    DenseLinkedList cq = new DenseLinkedList();
                    for (char c : str.toCharArray()) {
                        cq.push(c);
                    }
                    return cq.convertToString().equals("CharLinkedList[\"" +
                                                       str + "\"]");
                }
            },
            new TestGroup("charstack") {
                @Override
                protected boolean mainTest() {
                    String backwards = "\u266b sdrawkcaB";
                    String forwards = "Backwards \u266b";
                    DenseLinkedList cq = new DenseLinkedList();
                    for (char c : backwards.toCharArray()) {
                        cq.push(c);
                    }
                    return cq.convertToReversedString().equals(
                                   "CharLinkedList[\"" + forwards + "\"]");
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
