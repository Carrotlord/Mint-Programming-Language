package test;

/**
 * Example tests to make sure TestRunner is working. The final subtest
 * will fail on purpose.
 * @author Jiangcheng Oliver Chu
 */
public class ExampleTests extends TestGroup {
    public ExampleTests() {
        super("example_tests");
        TestGroup[] tests = {
            new TestGroup("equality") {
                @Override
                protected boolean mainTest() {
                    return 3 == 3;
                }
            },
            new TestGroup("null") {
                @Override
                protected boolean mainTest() {
                    return new Object() != null;
                }
            },
            new TestGroup("always_fails") {
                @Override
                protected boolean mainTest() {
                    return false;
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
