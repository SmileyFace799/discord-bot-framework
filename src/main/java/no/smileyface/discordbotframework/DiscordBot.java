package no.smileyface.discordbotframework;

import java.util.Arrays;
import java.util.Collection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.files.properties.PropertyLoadException;
import no.smileyface.discordbotframework.files.properties.PropertyLoader;

/**
 * Utility class for creating the discord bot.
 */
public class DiscordBot {
	private final JDA jda;
	private final Node<String, String> properties;

	/**
	 * Creates the discord bot. To create a bot with a custom{@link ActionManager},
	 * use {@link #DiscordBot(ActionManager, GatewayIntent...)} instead.
	 *
	 * @param actions Collection of actions to create the bot with.
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #DiscordBot(ActionManager, GatewayIntent...)
	 */
	public DiscordBot(
			Collection<? extends BotAction<? extends BotAction.ArgKey>> actions,
			GatewayIntent... intents
	) throws InterruptedException, PropertyLoadException {
		this(new ActionManager(actions), intents);
	}

	/**
	 * Creates the discord bot.
	 *
	 * @param actionManager The {@link ActionManager} for executing actions
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #DiscordBot(Collection, GatewayIntent...)
	 */
	public DiscordBot(
			ActionManager actionManager,
			GatewayIntent... intents
	) throws InterruptedException, PropertyLoadException {
		this.properties = PropertyLoader.loadProperties();
		Node<String, String> botNode = properties.getChild("bot");
		String botToken = botNode.getChild(botNode.getChild("active").getValue()).getValue();

		JDABuilder builder = JDABuilder
				.createDefault(botToken, Arrays.asList(intents))
				.disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
				.addEventListeners(actionManager);
		if (!Arrays.asList(intents).contains(GatewayIntent.GUILD_VOICE_STATES)) {
			builder.disableCache(CacheFlag.VOICE_STATE);
		}
		this.jda = builder.build();
		jda.awaitReady();
		actionManager.addCommands(jda.updateCommands()).queue();
	}

	public JDA getJda() {
		return jda;
	}

	public Node<String, String> getProperties() {
		return properties;
	}
}