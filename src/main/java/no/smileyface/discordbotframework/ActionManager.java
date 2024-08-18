package no.smileyface.discordbotframework;

import java.util.Collection;
import java.util.function.Function;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.ActionModal;
import no.smileyface.discordbotframework.entities.ActionSelection;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.Identifiable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for events & runs corresponding actions.
 * See below for which {@link ListenerAdapter} methods are implemented
 *
 * @see #onReady(ReadyEvent)
 * @see #onSlashCommandInteraction(SlashCommandInteractionEvent)
 * @see #onButtonInteraction(ButtonInteractionEvent)
 * @see #onModalInteraction(ModalInteractionEvent)
 * @see #onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent)
 */
public class ActionManager extends ListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);

	private final Collection<? extends BotAction<? extends BotAction.ArgKey>> actions;
	private final String defaultNotFoundMessage;

	/**
	 * Constructor.
	 *
	 * @param actions The collection of actions that the bot can perform.
	 * @see #ActionManager(ActionInitializer)
	 */
	public ActionManager(Collection<? extends BotAction<? extends BotAction.ArgKey>> actions) {
		this(manager -> actions);
	}

	/**
	 * Constructor.
	 *
	 * @param actionInitializer Initializer for all bot actions
	 * @see #ActionManager(Collection)
	 */
	public ActionManager(ActionInitializer actionInitializer) {
		this.actions = actionInitializer.createActions(this);
		this.defaultNotFoundMessage = "Oops, the bot doesn't know how to respond to "
				+ "whatever you just did. Please contact the bot owner";
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
	 * Runs {@link CommandListUpdateAction#addCommands(CommandData...)}
	 * on every command associated with a {@link BotAction}. This will not queue the update action
	 *
	 * @param updateAction The command update action
	 * @return The same update action that was passed, but with all commands added
	 */
	final CommandListUpdateAction addCommands(CommandListUpdateAction updateAction) {
		return updateAction.addCommands(actions
				.stream()
				.flatMap(action -> action.getCommands().stream())
				.flatMap(command -> command.getAllVariants().stream())
				.toList()
		);
	}

	/**
	 * Handles an incoming ready event. Fired whenever the bot comes online.
	 * This can be overridden to add custom behavior.
	 *
	 * @param event THe incoming {@link ReadyEvent}
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event) {
		LOGGER.info("{} is ready", event.getJDA().getSelfUser().getName());
	}

	private void onActionEvent(IReplyCallback event, String notFoundMessage) {
		actions.stream().filter(action -> action.belongsTo(event)).findFirst().ifPresentOrElse(
				action -> action.run(event),
				() -> event
						.reply(notFoundMessage == null ? defaultNotFoundMessage : notFoundMessage)
						.setEphemeral(true)
						.queue()
		);
	}

	private void onActionEvent(IReplyCallback event) {
		onActionEvent(event, null);
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
		onActionEvent(event);
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		onActionEvent(event, event.getComponentId().startsWith("--")
				? "This button has expired"
				: defaultNotFoundMessage
		);
	}

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		onActionEvent(event);
	}

	@Override
	public void onGenericSelectMenuInteraction(@NotNull GenericSelectMenuInteractionEvent event) {
		onActionEvent(event);
	}
}
