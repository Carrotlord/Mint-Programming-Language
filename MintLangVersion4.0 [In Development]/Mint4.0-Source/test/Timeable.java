package test;

/**
 * Represents a class with a run() method that can be timed.
 * @author Jiangcheng Oliver Chu
 */
public abstract class Timeable {
    private static final double NANOSECONDS_PER_SEC = 1e9;
    
    public abstract boolean run();
    
    public double getTimeTaken() {
        long startTime = System.nanoTime();
        run();
        return (System.nanoTime() - startTime) / NANOSECONDS_PER_SEC;
    }
}
