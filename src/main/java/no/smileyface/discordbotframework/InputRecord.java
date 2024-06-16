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

	/**
	 * Find a command by its name.
	 *
	 * @param commandName The name of the command to find
	 * @return The command found, or {@code null} if not found
	 */
	public ActionCommand<? extends BotAction.ArgKey> findCommand(String commandName) {
		return commands
				.stream()
				.filter(command -> command.identify(commandName))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Find a button by its ID.
	 *
	 * @param buttonId The ID of the button to find
	 * @return The button found, or {@code null} if not found
	 */
	public ActionButton<? extends BotAction.ArgKey> findButton(String buttonId) {
		return buttons
				.stream()
				.filter(button -> button.identify(buttonId))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Find a modal by its ID.
	 *
	 * @param modalId The ID of the modal to find
	 * @return The modal found, or {@code null} if not found
	 */
	public ActionModal<? extends BotAction.ArgKey> findModal(String modalId) {
		return modals
				.stream()
				.filter(modal -> modal.identify(modalId))
				.findFirst()
				.orElse(null);
	}
}
