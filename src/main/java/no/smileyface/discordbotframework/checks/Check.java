package no.smileyface.discordbotframework.checks;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

/**
 * Determines if a reply-able event can be processed.
 *
 * @see CheckFailedException
 */
public interface Check {
	/**
	 * Checks a specific event with this check.
	 *
	 * @param event The invocation event
	 * @throws CheckFailedException If the bot is connected to a voice channel in the server
	 *                               corresponding to the specified audio manager.
	 */
	void check(IReplyCallback event) throws CheckFailedException;
}
