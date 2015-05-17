package test;

import java.util.HashMap;
import mint.Mint;

/**
 * A collection of tests and subtests.
 * @author Jiangcheng Oliver Chu
 */
public abstract class TestGroup extends Timeable {
    private final String name;
    private TestGroup[] subTests;
    private boolean hasFailed = false;
    private String failureMessage = null;
    private TestGroup childTest = null;
    private static final String NO_CHILDREN = "@(no-children)";
    
    public TestGroup(String identifier, TestGroup[] tests) {
        name = identifier;
        setSubtests(tests);
        if (!identifier.equals(NO_CHILDREN)) {
            childTest = new AlwaysPasses();
        }
    }
    
    public TestGroup(String identifier) {
        this(identifier, new TestGroup[]{});
    }
    
    protected TestGroup findTest(String name) {
        for (TestGroup test : subTests) {
            if (test.getName().equals(name)) {
                return test;
            }
        }
        return null;
    }
    
    protected void setChildTest(TestGroup child) {
        childTest = child;
    }
    
    protected void setFailureMessage(String message) {
        failureMessage = message;
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
    
    @Override
    public boolean run() {
        updateFailedStatus(mainTest());
        if (getHasFailed()) {
            Mint.IO.printerr(failedMessage());
            return false;
        }
        for (TestGroup subTest : subTests) {
            updateFailedStatus(subTest.run());
            if (getHasFailed()) {
                Mint.IO.printerr(failedMessage(subTest,
                    "see above for reason"
                ));
                return false;
            }
        }
        return childTest.run();
    }
    
    public void addSelfTo(HashMap<String, TestGroup> map) {
        map.put(getName(), this);
    }
    
    protected boolean assertEquals(Object a, Object b) {
        if (a.equals(b)) {
            return true;
        } else {
            setFailureMessage(a + " not equal to " + b);
            return false;
        }
    }
    
    private class AlwaysPasses extends TestGroup {
        public AlwaysPasses() {
            super(NO_CHILDREN);
        }
        
        @Override
        protected boolean mainTest() {
            return true;
        }
        
        @Override
        public boolean run() {
            return true;
        }
    }
}
