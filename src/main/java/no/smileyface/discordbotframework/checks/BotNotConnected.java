package no.smileyface.discordbotframework.checks;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

/**
 * <p>Checks if the bot is not connected to a voice channel in a server.</p>
 * <p>The following checks are also performed implicitly:</p>
 * <ul>
 *     <li>{@link InGuild}</li>
 * </ul>
 */
public class BotNotConnected implements Check {
	private final InGuild inGuild;

	public BotNotConnected() {
		this.inGuild = new InGuild();
	}

	@Override
	public void check(IReplyCallback event) throws ChecksFailedException {
		if (inGuild.checkAndReturn(event).getGuild().getAudioManager().isConnected()) {
			throw new ChecksFailedException(
					"The bot is already connected to another voice channel");
		}
	}
}
