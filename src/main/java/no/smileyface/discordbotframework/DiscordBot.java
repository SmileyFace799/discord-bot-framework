package no.smileyface.discordbotframework;

import java.nio.file.NoSuchFileException;
import java.util.Arrays;
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
	 * {@link ActionManager}, use {@link #create(ActionManager, GatewayIntent...)} instead.
	 *
	 * @param actions Collection of actions to create the bot with.
	 * @return The created bot JDA instance
	 * @throws NoSuchFileException  If the token file for the active bot is not found
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #create(ActionManager, GatewayIntent...)
	 */
	public static JDA create(
			Collection<? extends BotAction<? extends BotAction.ArgKey>> actions,
			GatewayIntent... intents
	) throws NoSuchFileException, InterruptedException {
		return create(new ActionManager(actions), intents);
	}

	/**
	 * Creates the discord bot.
	 *
	 * @param actionManager The {@link ActionManager} for executing actions
	 * @return The created bot JDA instance
	 * @throws NoSuchFileException  If the token file for the active bot is not found
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #create(Collection, GatewayIntent...)
	 */
	public static JDA create(
			ActionManager actionManager,
			GatewayIntent... intents
	) throws NoSuchFileException, InterruptedException {
		JDABuilder builder = JDABuilder
				.createDefault(TokenManager.getActiveBot(), Arrays.asList(intents))
				.disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
				.addEventListeners(actionManager);
		if (!Arrays.asList(intents).contains(GatewayIntent.GUILD_VOICE_STATES)) {
			builder.disableCache(CacheFlag.VOICE_STATE);
		}
		JDA jda = builder.build();
		jda.awaitReady();
		actionManager.addCommands(jda.updateCommands()).queue();

		return jda;
	}
}