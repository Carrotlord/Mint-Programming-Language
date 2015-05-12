package test;

import mint.CharQueue;

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
                    CharQueue cq = new CharQueue();
                    for (char c : str.toCharArray()) {
                        cq.push(c);
                    }
                    return cq.convertToString().equals("CharQueue[\"" + str +
                                                       "\"]");
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
