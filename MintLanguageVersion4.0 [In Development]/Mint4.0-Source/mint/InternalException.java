package mint;

/**
 * An exception that should not be triggered due to user error; should
 * indicate an actual bug in the program.
 * @author Jiangcheng Oliver Chu
 */
public class InternalException extends Exception {
    public InternalException(String message) {
        super(message);
    }
}
