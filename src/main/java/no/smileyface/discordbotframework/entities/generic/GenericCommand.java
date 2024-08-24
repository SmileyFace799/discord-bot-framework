package no.smileyface.discordbotframework.entities.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.Identifiable;

/**
 * A slash command for executing actions.
 *
 * @param <K> Key type used for args returned from
 * 			  {@link #getSlashArgs(SlashCommandInteractionEvent)}.
 */
public abstract class GenericCommand<K extends GenericBotAction.ArgKey> implements Identifiable {
	private final SlashCommandData data;
	private final Collection<String> nicknames;

	/**
	 * Constructor.
	 *
	 * @param data Command data for registering the slash command
	 * @param nicknames Any nicknames for the command
	 */
	protected GenericCommand(SlashCommandData data, String... nicknames) {
		this.data = data;
		this.nicknames = nicknames == null
				? Set.of()
				: Arrays.stream(nicknames).map(String::toLowerCase).toList();
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
		nicknames.forEach(nickname -> variations.add(Commands
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
	 * <p>This returns an empty node by default.</p>
	 *
	 * @param event The command event representing the executed command
	 * @return Node of action arguments
	 */
	public Node<K, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		return new Node<>();
	}

	@Override
	public final boolean identify(String name) {
		return data.getName().equalsIgnoreCase(name) || nicknames.contains(name.toLowerCase());
	}
}
