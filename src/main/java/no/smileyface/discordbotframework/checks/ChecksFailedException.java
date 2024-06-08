package no.smileyface.discordbotframework.checks;

/**
 * Thrown if a reply-able event cannot be processed.
 */
public class ChecksFailedException extends Exception {
    public ChecksFailedException(String message) {
        super(message);
    }
}
