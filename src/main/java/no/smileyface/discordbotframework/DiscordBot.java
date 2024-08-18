package no.smileyface.discordbotframework;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
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
	 * use {@link #DiscordBot(ActionManager, Consumer, GatewayIntent...)} instead.
	 *
	 * @param actions Collection of actions to create the bot with.
	 * @param propertiesHook A hook that is run immediately after properties have loaded
	 * @param intents        Any {@link GatewayIntent}s the bot requires
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #DiscordBot(ActionManager, Consumer, GatewayIntent...)
	 */
	public DiscordBot(
			Collection<? extends BotAction<? extends BotAction.ArgKey>> actions,
			Consumer<Node<String, String>> propertiesHook,
			GatewayIntent... intents
	) throws InterruptedException, PropertyLoadException {
		this(new ActionManager(actions), propertiesHook, intents);
	}

	/**
	 * Creates the discord bot.
	 *
	 * @param actionManager The {@link ActionManager} for executing actions
	 * @param propertiesHook A hook that is run immediately after properties have loaded
	 * @param intents        Any {@link GatewayIntent}s the bot requires
	 * @throws InterruptedException If the bot is interrupted while starting
	 * @see #DiscordBot(Collection, Consumer, GatewayIntent...)
	 */
	public DiscordBot(
			ActionManager actionManager,
			Consumer<Node<String, String>> propertiesHook,
			GatewayIntent... intents
	) throws InterruptedException, PropertyLoadException {
		this.properties = PropertyLoader.loadProperties();
		if (propertiesHook != null) {
			propertiesHook.accept(properties);
		}
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