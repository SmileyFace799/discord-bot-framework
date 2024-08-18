package no.smileyface.discordbotframework.checks;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

/**
 * Same as {@link Check}, except a value is returned,
 * typically useful for other checks that have this as an implicit one.
 *
 * @param <R> The return type of the check
 */
public interface CheckAndReturn<R> extends Check {
	R checkAndReturn(IReplyCallback event) throws CheckFailedException;

	@Override
	default void check(IReplyCallback event) throws CheckFailedException {
		checkAndReturn(event);
	}
}
