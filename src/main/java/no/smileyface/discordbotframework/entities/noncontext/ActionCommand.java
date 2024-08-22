package no.smileyface.discordbotframework.entities.noncontext;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.generic.GenericCommand;

/**
 * A slash command for executing regular actions.
 * {@inheritDoc}
 */
public class ActionCommand<K extends GenericBotAction.ArgKey> extends GenericCommand<K> {
	/**
	 * Constructor.
	 *
	 * @param data Command data for registering the slash command
	 * @param nicknames Any nicknames for the command
	 */
	public ActionCommand(SlashCommandData data, String... nicknames) {
		super(data, nicknames);
	}
}
