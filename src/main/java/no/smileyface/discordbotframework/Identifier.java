package no.smileyface.discordbotframework;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
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

	private static <I extends Identifiable, T extends Identifiable> Optional<T> identify(
			Stream<I> identifiables,
			Class<T> targetClass
	) {
		return identifiables
				.filter(identifiable -> identifiable.getClass() == targetClass)
				.map(targetClass::cast)
				.findFirst();
	}

	/**
	 * Finds an identifiable in a collection of identifiables.
	 *
	 * @param identifiables The collection of identifiables to search through
	 * @param targetClass The target class of the identifiable to find
	 * @param <I> The type of the identifiable to find
	 * @param <T> The type of identifiables in the collection provided
	 * @return An optional containing the target identifiable if found
	 */
	public static <I extends Identifiable, T extends I> Optional<T> identify(
			Collection<I> identifiables,
			Class<T> targetClass
	) {
		return identify(identifiables.stream(), targetClass);
	}

	private <I extends Identifiable, T extends I> Optional<T> findIdentifiable(
			Function<BotAction<? extends BotAction.ArgKey>, Collection<? extends I>> getFunction,
			Class<T> targetClass
	) {
		return identify(
				actions
						.stream()
						.flatMap(action -> getFunction.apply(action).stream()),
				targetClass
		);
	}

	private <I extends Identifiable> Optional<I> findIdentifiable(
			Function<BotAction<? extends BotAction.ArgKey>, Collection<I>> getFunction,
			String id
	) {
		return actions.stream()
				.flatMap(action -> getFunction.apply(action).stream())
				.filter(identifiable -> identifiable.identify(id))
				.findFirst();
	}

	/**
	 * Find a command by its class.
	 *
	 * @param commandClass The class of the command to find
	 * @return The command found, or {@code null} if not found
	 */
	public final <C extends ActionCommand<? extends BotAction.ArgKey>> Optional<C> findCommand(
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
	public final Optional<? extends ActionCommand<? extends BotAction.ArgKey>> findCommand(
			String commandName
	) {
		return findIdentifiable(BotAction::getCommands, commandName);
	}

	/**
	 * Find a button by its class.
	 *
	 * @param buttonClass The class of the button to find
	 * @return The button found, or {@code null} if not found
	 */
	public final <B extends ActionButton<? extends BotAction.ArgKey>> Optional<B> findButton(
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
	public final Optional<? extends ActionButton<? extends BotAction.ArgKey>> findButton(
			String buttonId
	) {
		return findIdentifiable(BotAction::getButtons, buttonId);
	}

	/**
	 * Find a modal by its class.
	 *
	 * @param modalClass The class of the modal to find
	 * @return The modal found, or {@code null} if not found
	 */
	public final <M extends ActionModal<? extends BotAction.ArgKey>> Optional<M> findModal(
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
	public final Optional<? extends ActionModal<? extends BotAction.ArgKey>> findModal(
			String modalId
	) {
		return findIdentifiable(BotAction::getModals, modalId);
	}

	/**
	 * Find a selection by its class.
	 *
	 * @param selectionClass The class of the selection to find
	 * @return The selection found, or {@code null} if not found
	 */
	public final <S extends ActionSelection<? extends BotAction.ArgKey>> Optional<S> findSelection(
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
	public final Optional<? extends ActionSelection<? extends BotAction.ArgKey>> findSelection(
			String selectionId
	) {
		return findIdentifiable(BotAction::getSelections, selectionId);
	}

	private Optional<? extends BotAction<? extends BotAction.ArgKey>> findAction(
			Function<BotAction<? extends BotAction.ArgKey>,
					Collection<? extends Identifiable>> getFunction,
			String id
	) {
		return actions
				.stream()
				.filter(action -> getFunction
						.apply(action)
						.stream()
						.anyMatch(identifiable -> identifiable.identify(id))
				).findFirst();
	}

	/**
	 * Find an action by a received event.
	 *
	 * @param event The incoming event to find a corresponding action for
	 * @return The action that should run from the event
	 */
	public final Optional<? extends BotAction<? extends BotAction.ArgKey>> findAction(
			IReplyCallback event
	) {
		return switch (event) {
			case SlashCommandInteractionEvent slashEvent ->
					findAction(BotAction::getCommands, slashEvent.getName());
			case ButtonInteractionEvent buttonEvent ->
					findAction(BotAction::getButtons, buttonEvent.getComponentId());
			case ModalInteractionEvent modalEvent ->
					findAction(BotAction::getModals, modalEvent.getModalId());
			case GenericSelectMenuInteractionEvent<?, ?> selectionEvent ->
					findAction(BotAction::getSelections, selectionEvent.getComponentId());
			default -> Optional.empty();
		};
	}
}
