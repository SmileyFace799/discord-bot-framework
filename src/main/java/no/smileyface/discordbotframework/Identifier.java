package no.smileyface.discordbotframework;

import java.util.Collection;
import java.util.function.Function;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.ActionModal;
import no.smileyface.discordbotframework.entities.ActionSelection;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.Identifiable;

/**
 * Finds identifiable entities, either by class name (preferred) or name/id.
 */
public class Identifier {
	private final Collection<? extends BotAction<? extends BotAction.ArgKey>> actions;

	Identifier(Collection<? extends BotAction<? extends BotAction.ArgKey>> actions) {
		this.actions = actions;
	}

	private <I extends Identifiable, T extends Identifiable> T findIdentifiable(
			Function<BotAction<? extends BotAction.ArgKey>, Collection<I>> getFunction,
			Class<T> targetClass
	) {
		return actions.stream()
				.flatMap(action -> getFunction.apply(action).stream())
				.filter(identifiable -> identifiable.getClass() == targetClass)
				.map(targetClass::cast)
				.findFirst()
				.orElse(null);
	}

	private <I extends Identifiable> I findIdentifiable(
			Function<BotAction<? extends BotAction.ArgKey>, Collection<I>> getFunction,
			String id
	) {
		return actions.stream()
				.flatMap(action -> getFunction.apply(action).stream())
				.filter(identifiable -> identifiable.identify(id))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Find a command by its class.
	 *
	 * @param commandClass The class of the command to find
	 * @return The command found, or {@code null} if not found
	 */
	public final <C extends ActionCommand<? extends BotAction.ArgKey>> C findCommand(
			Class<C> commandClass
	) {
		return findIdentifiable(BotAction::getCommands, commandClass);
	}

	/**
	 * Find a command by its name.
	 *
	 * @param commandName The name of the command to find
	 * @return The command found, or {@code null} if not found
	 */
	public final ActionCommand<? extends BotAction.ArgKey> findCommand(String commandName) {
		return findIdentifiable(BotAction::getCommands, commandName);
	}

	/**
	 * Find a button by its class.
	 *
	 * @param buttonClass The class of the button to find
	 * @return The button found, or {@code null} if not found
	 */
	public final <B extends ActionButton<? extends BotAction.ArgKey>> B findButton(
			Class<B> buttonClass
	) {
		return findIdentifiable(BotAction::getButtons, buttonClass);
	}

	/**
	 * Find a button by its ID.
	 *
	 * @param buttonId The ID of the button to find
	 * @return The button found, or {@code null} if not found
	 */
	public final ActionButton<? extends BotAction.ArgKey> findButton(String buttonId) {
		return findIdentifiable(BotAction::getButtons, buttonId);
	}

	/**
	 * Find a modal by its class.
	 *
	 * @param modalClass The class of the modal to find
	 * @return The modal found, or {@code null} if not found
	 */
	public final <M extends ActionModal<? extends BotAction.ArgKey>> M findModal(
			Class<M> modalClass
	) {
		return findIdentifiable(BotAction::getModals, modalClass);
	}

	/**
	 * Find a modal by its ID.
	 *
	 * @param modalId The ID of the modal to find
	 * @return The modal found, or {@code null} if not found
	 */
	public final ActionModal<? extends BotAction.ArgKey> findModal(String modalId) {
		return findIdentifiable(BotAction::getModals, modalId);
	}

	/**
	 * Find a selection by its class.
	 *
	 * @param selectionClass The class of the selection to find
	 * @return The selection found, or {@code null} if not found
	 */
	public final <S extends ActionSelection<? extends BotAction.ArgKey>> S findSelection(
			Class<S> selectionClass
	) {
		return findIdentifiable(BotAction::getSelections, selectionClass);
	}

	/**
	 * Find a selection by its ID.
	 *
	 * @param selectionId The ID of the selection to find
	 * @return The selection found, or {@code null} if not found
	 */
	public final ActionSelection<? extends BotAction.ArgKey> findSelection(String selectionId) {
		return findIdentifiable(BotAction::getSelections, selectionId);
	}
}
