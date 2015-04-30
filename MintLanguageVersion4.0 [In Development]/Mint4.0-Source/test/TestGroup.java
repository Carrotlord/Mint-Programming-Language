package test;

import java.util.HashMap;
import mint.Mint;

/**
 *
 * @author Jiangcheng Oliver Chu
 */
public abstract class TestGroup {
    private final String name;
    private TestGroup[] subTests;
    private boolean hasFailed = false;
    private String failureMessage = null;
    
    public TestGroup(String identifier, TestGroup[] tests) {
        name = identifier;
        setSubtests(tests);
    }
    
    protected void setFailureMessage(String message) {
        failureMessage = message;
    }
    
    public TestGroup(String identifier) {
        this(identifier, new TestGroup[]{});
    }
    
    public final void setSubtests(TestGroup[] tests) {
        subTests = tests;
    }
    
    public String getName() {
        return name;
    }
    
    protected abstract boolean mainTest();
    
    /**
     * Overwrite this to give a custom failed message.
     * @return message upon failure
     */
    protected String failedMessage() {
        return failedMessage(null, failureMessage);
    }
    
    /**
     * 
     * @param subTest
     * @param reason
     * @return 
     */
    protected String failedMessage(TestGroup subTest, String reason) {
        String subTestName;
        if (subTest != null) {
            subTestName = subTest.getName();
        } else {
            subTestName = "<main test>";
        }
        String failedReason;
        if (reason == null || reason.length() == 0) {
            failedReason = "unspecified reason";
        } else {
            failedReason = reason;
        }
        return "Test " + getName() + "." + subTestName + " failed. (" +
               failedReason + ")";
    }
    
    private boolean updateFailedStatus(boolean nextTestResult) {
        hasFailed = hasFailed || !nextTestResult;
        return hasFailed;
    }
    
    private boolean getHasFailed() {
        return hasFailed;
    }
    
    public boolean run() {
        updateFailedStatus(mainTest());
        if (getHasFailed()) {
            Mint.printerr(failedMessage());
            return false;
        }
        for (int i = 0; i < subTests.length; i++) {
            updateFailedStatus(subTests[i].run());
            if (getHasFailed()) {
                Mint.printerr(failedMessage(subTests[i],
                              "see above for reason"));
                return false;
            }
        }
        return true;
    }
    
    public void addSelfTo(HashMap<String, TestGroup> map) {
        map.put(getName(), this);
    }
}
