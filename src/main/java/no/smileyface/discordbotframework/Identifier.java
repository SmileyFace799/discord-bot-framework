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
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.Identifiable;
import no.smileyface.discordbotframework.entities.generic.GenericButton;
import no.smileyface.discordbotframework.entities.generic.GenericCommand;
import no.smileyface.discordbotframework.entities.generic.GenericModal;
import no.smileyface.discordbotframework.entities.generic.GenericSelection;

/**
 * Finds identifiable entities, either by class name (preferred) or name/id.
 */
public class Identifier {
	private final Collection<? extends GenericBotAction<?, ?, ?, ?, ?>> actions;

	Identifier(Collection<? extends GenericBotAction<?, ?, ?, ?, ?>> actions) {
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
	 * @param targetClass   The target class of the identifiable to find
	 * @param <I>           The type of identifiables in the collection provided
	 * @param <T>           The type of the identifiable to find
	 * @return An optional containing the target identifiable if found
	 */
	public static <I extends Identifiable, T extends I> Optional<T> identify(
			Collection<I> identifiables,
			Class<T> targetClass
	) {
		return identify(identifiables.stream(), targetClass);
	}

	private static <I extends Identifiable> Optional<I> identify(
			Stream<I> identifiables,
			String id
	) {
		return identifiables
				.filter(identifiable -> identifiable.identify(id))
				.findFirst();
	}

	/**
	 * Finds an identifiable in a collection of identifiables.
	 *
	 * @param identifiables The collection of identifiables to search through
	 * @param id            The id of the identifiable to find
	 * @param <I>           The type of identifiables in the collection provided
	 * @return An optional containing the target identifiable if found
	 */
	public static <I extends Identifiable> Optional<I> identify(
			Collection<I> identifiables,
			String id
	) {
		return identify(identifiables.stream(), id);
	}

	private <I extends Identifiable, T extends I> Optional<T> findIdentifiable(
			Function<GenericBotAction<?, ?, ?, ?, ?>, Collection<? extends I>> getFunction,
			Class<T> targetClass
	) {
		return identify(
				actions.stream().flatMap(action -> getFunction.apply(action).stream()),
				targetClass
		);
	}

	private <I extends Identifiable> Optional<I> findIdentifiable(
			Function<GenericBotAction<?, ?, ?, ?, ?>, Collection<I>> getFunction,
			String id
	) {
		return identify(
				actions.stream().flatMap(action -> getFunction.apply(action).stream()),
				id
		);
	}

	/**
	 * Find a command by its class.
	 *
	 * @param commandClass The class of the command to find
	 * @return The command found, or {@code null} if not found
	 */
	public final <C extends GenericCommand<?>> Optional<C> findCommand(Class<C> commandClass) {
		return findIdentifiable(GenericBotAction::getCommands, commandClass);
	}

	/**
	 * Find a command by its name.
	 *
	 * @param commandName The name of the command to find
	 * @return The command found, or {@code null} if not found
	 */
	public final Optional<? extends GenericCommand<?>> findCommand(String commandName) {
		return findIdentifiable(GenericBotAction::getCommands, commandName);
	}

	/**
	 * Find a button by its class.
	 *
	 * @param buttonClass The class of the button to find
	 * @return The button found, or {@code null} if not found
	 */
	public final <B extends GenericButton<?>> Optional<B> findButton(Class<B> buttonClass) {
		return findIdentifiable(GenericBotAction::getButtons, buttonClass);
	}

	/**
	 * Find a button by its ID.
	 *
	 * @param buttonId The ID of the button to find
	 * @return The button found, or {@code null} if not found
	 */
	public final Optional<? extends GenericButton<?>> findButton(String buttonId) {
		return findIdentifiable(GenericBotAction::getButtons, buttonId);
	}

	/**
	 * Find a modal by its class.
	 *
	 * @param modalClass The class of the modal to find
	 * @return The modal found, or {@code null} if not found
	 */
	public final <M extends GenericModal<?>> Optional<M> findModal(Class<M> modalClass) {
		return findIdentifiable(GenericBotAction::getModals, modalClass);
	}

	/**
	 * Find a modal by its ID.
	 *
	 * @param modalId The ID of the modal to find
	 * @return The modal found, or {@code null} if not found
	 */
	public final Optional<? extends GenericModal<?>> findModal(String modalId) {
		return findIdentifiable(GenericBotAction::getModals, modalId);
	}

	/**
	 * Find a selection by its class.
	 *
	 * @param selectionClass The class of the selection to find
	 * @return The selection found, or {@code null} if not found
	 */
	public final <S extends GenericSelection<?>> Optional<S> findSelection(
			Class<S> selectionClass
	) {
		return findIdentifiable(GenericBotAction::getSelections, selectionClass);
	}

	/**
	 * Find a selection by its ID.
	 *
	 * @param selectionId The ID of the selection to find
	 * @return The selection found, or {@code null} if not found
	 */
	public final Optional<? extends GenericSelection<?>> findSelection(String selectionId) {
		return findIdentifiable(GenericBotAction::getSelections, selectionId);
	}

	private Optional<? extends GenericBotAction<?, ?, ?, ?, ?>> findAction(
			Function<GenericBotAction<?, ?, ?, ?, ?>,
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
	public final Optional<? extends GenericBotAction<?, ?, ?, ?, ?>> findAction(
			IReplyCallback event
	) {
		return switch (event) {
			case SlashCommandInteractionEvent slashEvent ->
					findAction(GenericBotAction::getCommands, slashEvent.getName());
			case ButtonInteractionEvent buttonEvent ->
					findAction(GenericBotAction::getButtons, buttonEvent.getComponentId());
			case ModalInteractionEvent modalEvent ->
					findAction(GenericBotAction::getModals, modalEvent.getModalId());
			case GenericSelectMenuInteractionEvent<?, ?> selectionEvent ->
					findAction(GenericBotAction::getSelections, selectionEvent.getComponentId());
			default -> Optional.empty();
		};
	}
}
