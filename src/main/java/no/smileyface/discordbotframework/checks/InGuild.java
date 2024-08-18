package no.smileyface.discordbotframework.checks;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

/**
 * Checks if the event was fired in a guild.
 */
public class InGuild implements CheckAndReturn<Member> {
	@Override
	public Member checkAndReturn(IReplyCallback event) throws CheckFailedException {
		if (!event.isFromGuild()) {
			throw new CheckFailedException("You're not in a server");
		}
		return Objects.requireNonNull(event.getMember());
	}
}
