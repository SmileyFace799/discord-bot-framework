package no.smileyface.discordbotframework.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import no.smileyface.discordbotframework.misc.Identifiable;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * A slash command for executing actions.
 *
 * @param <K> Key type used for args returned from
 * 			  {@link #getSlashArgs(SlashCommandInteractionEvent)}.
 */
public class ActionCommand<K extends BotAction.ArgKey> implements Identifiable {
	private final SlashCommandData data;
	private final Collection<String> names;

	/**
	 * Constructor.
	 *
	 * @param data Command data for registering the slash command
	 * @param nicknames Any nicknames for the command
	 */
	public ActionCommand(SlashCommandData data, String... nicknames) {
		this.data = data;
		this.names = new HashSet<>(nicknames == null
				? Set.of()
				: Arrays.stream(nicknames).map(String::toLowerCase).toList());
		this.names.add(data.getName());
	}

	/**
	 * Get all variants of the command.
	 * These variants will be identical to the original command, except the name.
	 *
	 * @return A list of command variants, one for each nickname in the `nicknames`-collection
	 *         passed to the constructor.
	 *         The returned collection will also include this command itself
	 */
	public final Collection<CommandData> getAllVariants() {
		Collection<CommandData> variations = new HashSet<>();
		variations.add(data);
		names.forEach(nickname -> variations.add(Commands
				.slash(nickname, "Shortcut for /" + data.getName())
				.addOptions(data.getOptions())
				.setGuildOnly(data.isGuildOnly())
				.setDefaultPermissions(data.getDefaultPermissions())
				.setNSFW(data.isNSFW()))
		);
		return variations;
	}

	/**
	 * Creates action arguments from a slash command event.
	 * <p>This returns an empty map by default.</p>
	 *
	 * @param event The command event representing the executed command
	 * @return Map of action arguments
	 */
	public MultiTypeMap<K> getSlashArgs(SlashCommandInteractionEvent event) {
		return new MultiTypeMap<>();
	}

	@Override
	public final boolean identify(String name) {
		return names.contains(name.toLowerCase());
	}
}
