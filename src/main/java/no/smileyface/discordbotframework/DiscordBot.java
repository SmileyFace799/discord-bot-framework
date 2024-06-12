package no.smileyface.discordbotframework;

import java.nio.file.NoSuchFileException;
import java.util.Collection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Utility class for creating the discord bot.
 */
public final class DiscordBot {
	private DiscordBot() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Creates the discord bot. To create a bot with a custom
	 * {@link ActionManager}, use {@link #create(ActionManager)} instead.
	 *
	 * @param actions Collection of actions to create the bot with.
	 * @return The created bot JDA instance
	 * @throws NoSuchFileException  If the token file for the active bot is not found
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #create(ActionManager)
	 */
	public static JDA create(
			Collection<? extends BotAction<? extends BotAction.ArgKey>> actions
	) throws NoSuchFileException, InterruptedException {
		return create(new ActionManager(actions));
	}

	/**
	 * Creates the discord bot.
	 *
	 * @param actionManager The {@link ActionManager} for executing actions
	 * @return The created bot JDA instance
	 * @throws NoSuchFileException  If the token file for the active bot is not found
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #create(Collection)
	 */
	public static JDA create(
			ActionManager actionManager
	) throws NoSuchFileException, InterruptedException {
		JDA jda = JDABuilder
				.createDefault(TokenManager.getActiveBot(), GatewayIntent.GUILD_VOICE_STATES)
				.disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
				.addEventListeners(actionManager)
				.build();

		jda.awaitReady();
		actionManager.addCommands(jda.updateCommands()).queue();

		return jda;
	}
}