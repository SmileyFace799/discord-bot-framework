package no.smileyface.discordbotframework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import no.smileyface.discordbotframework.entities.BotCommand;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for events. Some events have default implementations, listed below.
 *
 * @see #onReady(ReadyEvent)
 * @see #onSlashCommandInteraction(SlashCommandInteractionEvent)
 */
public class BotListener extends ListenerAdapter {
	private final Map<String, BotCommand> commands;
	private boolean initialized;

	/**
	 * Constructor.
	 */
	public BotListener() {
		this.commands = new HashMap<>();
		this.initialized = false;
	}

	/**
	 * Initializes the set of commands to be listened to by
	 * {@link #onSlashCommandInteraction(SlashCommandInteractionEvent)}.
	 * This method is called once with the {@link BotCommand}s provided to the
	 * {@link DiscordBot#DiscordBot(BotListener, Collection) DiscordBot Constructor}.
	 *
	 * @param commands The commands to initialize the listener with
	 */
	final void initializeCommands(Collection<BotCommand> commands) {
		this.commands.putAll(commands.stream().collect(Collectors.toMap(
				cmd -> cmd.getData().getName(),
				Function.identity()
		)));
		this.initialized = true;
	}

	/**
	 * Handles an incoming ready event. Fired whenever the bot comes online.
	 * This can be overridden to add custom behavior
	 *
	 * @param event THe incoming {@link ReadyEvent}
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event) {
		Logger.getLogger(getClass().getName()).log(
				Level.INFO, "{0} is ready", event.getJDA().getSelfUser().getName()
		);
	}

	/**
	 * Handles an incoming slash command. This can be overridden to add custom behavior,
	 * but this parent method should always be called in any overriding methods
	 * to actually execute the incoming command
	 *
	 * @param event The incoming {@link SlashCommandInteractionEvent}
	 *              representing an executed slash command
	 */
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (!initialized) {
			throw new IllegalStateException("An incoming command event was registered"
					+ "before commands were initialized");
		}
		BotCommand command = commands.get(event.getName());
		command.run(event, command.getSlashArgs(event));
	}
}
