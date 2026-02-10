package flora.exception;

/**
 * Represents an exception specific to the Flora application.
 */
public class FloraException extends Exception {
    /**
     * Constructs a FloraException with the specified error message.
     *
     * @param message The error message.
     */
    public FloraException(String message) {
        super(message);
    }
}
