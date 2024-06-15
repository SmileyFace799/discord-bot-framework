package no.smileyface.discordbotframework;

import java.util.Collection;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.ActionModal;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * A record containing all commands, buttons & modals.
 */
public class InputRecord {
	private final Collection<? extends ActionCommand<? extends BotAction.ArgKey>> commands;
	private final Collection<? extends ActionButton<? extends BotAction.ArgKey>> buttons;
	private final Collection<? extends ActionModal<? extends BotAction.ArgKey>> modals;

	InputRecord(Collection<? extends BotAction<? extends BotAction.ArgKey>> actions) {
		commands = actions.stream().flatMap(action -> action.getCommands().stream()).toList();
		buttons = actions.stream().flatMap(action -> action.getButtons().stream()).toList();
		modals = actions.stream().flatMap(action -> action.getModals().stream()).toList();
	}

	public Collection<? extends ActionCommand<? extends BotAction.ArgKey>> getCommands() {
		return commands;
	}

	public Collection<? extends ActionButton<? extends BotAction.ArgKey>> getButtons() {
		return buttons;
	}

	public Collection<? extends ActionModal<? extends BotAction.ArgKey>> getModals() {
		return modals;
	}
}
