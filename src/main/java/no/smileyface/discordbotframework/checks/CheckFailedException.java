package no.smileyface.discordbotframework.checks;

/**
 * Thrown if a reply-able event cannot be processed.
 */
public class CheckFailedException extends Exception {
    public CheckFailedException(String message) {
        super(message);
    }
}
