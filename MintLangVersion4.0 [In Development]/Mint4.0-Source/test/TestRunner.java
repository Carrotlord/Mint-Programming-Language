package test;

import mint.Mint;

/**
 *
 * @author Jiangcheng Oliver Chu
 */
public class TestRunner {
    private TestGroup[] allTests;
    
    public TestRunner(TestGroup[] allTestsToRun) {
        allTests = allTestsToRun;
    }
    
    public void runTests() {
        boolean allTestsPassed = true;
        for (TestGroup test : allTests) {
            if (!test.run()) {
                allTestsPassed = false;
                break;
            }
        }
        Mint.debugln("All tests passed? " +
                    (allTestsPassed ? "Yes" : "No, stopped on first failure"));
    }
}
