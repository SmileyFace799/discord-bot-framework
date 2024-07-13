package no.smileyface.discordbotframework;

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
	 * {@link ActionManager}, use {@link #create(String, ActionManager, GatewayIntent...)} instead.
	 *
	 * @param actions Collection of actions to create the bot with.
	 * @return The created bot JDA instance
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #create(String, ActionManager, GatewayIntent...)
	 */
	public static JDA create(
			String botToken,
			Collection<? extends BotAction<? extends BotAction.ArgKey>> actions,
			GatewayIntent... intents
	) throws InterruptedException {
		return create(botToken, new ActionManager(actions), intents);
	}

	/**
	 * Creates the discord bot.
	 *
	 * @param actionManager The {@link ActionManager} for executing actions
	 * @return The created bot JDA instance
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #create(String, Collection, GatewayIntent...)
	 */
	public static JDA create(
			String botToken,
			ActionManager actionManager,
			GatewayIntent... intents
	) throws InterruptedException {
		JDABuilder builder = JDABuilder
				.createDefault(botToken, Arrays.asList(intents))
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