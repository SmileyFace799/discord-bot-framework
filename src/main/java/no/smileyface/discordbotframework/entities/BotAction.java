package no.smileyface.discordbotframework.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.Identifier;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.checks.CheckFailedException;
import no.smileyface.discordbotframework.data.Node;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a basic bot action.
 * To invoke the action, action inputs need to be provided from the following methods:
 * <ul>
 *     <li>{@link #createCommands()}: Slash commands that should trigger this action</li>
 *     <li>{@link #createButtons()}: Buttons that should trigger this action</li>
 *     <li>{@link #createModals()}: Modals that should trigger this action when submitted</li>
 *     <li>{@link #createSelections()}: Selections that should trigger this action
 *     when submitted</li>
 * </ul>
 *
 * @param <K> Key type used for args given to {@link #execute(IReplyCallback, Node)}.
 */
public abstract class BotAction<K extends BotAction.ArgKey> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BotAction.class);

	private final ActionManager manager;
	private final Collection<ActionCommand<K>> commands;
	private final Collection<ActionButton<K>> buttons;
	private final Collection<ActionModal<K>> modals;
	private final Collection<ActionSelection<K>> selections;
	private final Collection<Check> checks;

	/**
	 * Creates the action.
	 *
	 * @param manager The {@link ActionManager} for this bot
	 * @param checks  Any {@link Check}s to set conditions that need to be met
	 *                for the action to go through
	 * @see no.smileyface.discordbotframework.ActionInitializer ActionInitializer
	 */
	protected BotAction(
			ActionManager manager,
			Check... checks
	) {
		this.manager = manager;
		// Commands should never change after initialization, and should therefore be immutables
		this.commands = Collections.unmodifiableCollection(createCommands());
		this.buttons = new HashSet<>(createButtons());
		this.modals = new HashSet<>(createModals());
		this.selections = new HashSet<>(createSelections());
		this.checks = checks == null ? Set.of() : Arrays.stream(checks).toList();
	}

	/**
	 * Returns an empty collection by default,
	 * but can be overridden to create {@link ActionCommand}s that should trigger this action.
	 * This should always create a new instance of the commands.
	 * <p>This method is called in the default implementation of the
	 * {@link BotAction#BotAction(ActionManager, Check...) constructor}.</p>
	 *
	 * @return A collection of commands that should trigger this action. Empty by default
	 */
	protected @NotNull Collection<? extends ActionCommand<K>> createCommands() {
		return Set.of();
	}

	/**
	 * Returns an empty collection by default,
	 * but can be overridden to create {@link ActionButton}s that should trigger this action.
	 * This should always create a new instance of the buttons.
	 * <p>This method is called in the default implementation of the
	 * {@link BotAction#BotAction(ActionManager, Check...) constructor}.</p>
	 *
	 * @return A collection of buttons that should trigger this action. Empty by default
	 */
	protected @NotNull Collection<? extends ActionButton<K>> createButtons() {
		return Set.of();
	}

	/**
	 * Returns an empty collection by default,
	 * but can be overridden to create {@link ActionModal}s that should trigger this action.
	 * This should always create a new instance of the modals.
	 * <p>This method is called in the default implementation of the
	 * {@link BotAction#BotAction(ActionManager, Check...) constructor}.</p>
	 *
	 * @return A collection of modals that should trigger this action. Empty by default
	 */
	protected @NotNull Collection<? extends ActionModal<K>> createModals() {
		return Set.of();
	}

	/**
	 * Returns an empty collection by default,
	 * but can be overridden to create {@link ActionSelection}s that should trigger this action.
	 * This should always create a new instance of the selections.
	 * <p>This method is called in the default implementation of the
	 * {@link BotAction#BotAction(ActionManager, Check...) constructor}.</p>
	 *
	 * @return A collection of selections that should trigger this action. Empty by default
	 */
	protected @NotNull Collection<? extends ActionSelection<K>> createSelections() {
		return Set.of();
	}

	protected final Identifier getIdentifier() {
		return manager.getIdentifier();
	}

	public final Collection<ActionCommand<K>> getCommands() {
		return commands;
	}

	public final Collection<ActionButton<K>> getButtons() {
		return Collections.unmodifiableCollection(buttons);
	}

	public final Collection<ActionModal<K>> getModals() {
		return Collections.unmodifiableCollection(modals);
	}

	public final Collection<ActionSelection<K>> getSelections() {
		return Collections.unmodifiableCollection(selections);
	}

	private ActionCommand<K> belongsTo(SlashCommandInteractionEvent event) {
		return commands
				.stream()
				.filter(command -> command.identify(event.getName()))
				.findFirst()
				.orElse(null);
	}

	private ActionButton<K> belongsTo(ButtonInteractionEvent event) {
		return buttons
				.stream()
				.filter(button -> button.identify(event.getComponentId()))
				.findFirst()
				.orElse(null);
	}

	private ActionModal<K> belongsTo(ModalInteractionEvent event) {
		return modals
				.stream()
				.filter(modal -> modal.identify(event.getModalId()))
				.findFirst()
				.orElse(null);
	}

	private ActionSelection<K> belongsTo(GenericSelectMenuInteractionEvent<?, ?> event) {
		return selections
				.stream()
				.filter(selection -> selection.identify(event.getComponentId()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Checks if any of the commands, buttons or modals that can invoke this action
	 * belongs to the event.
	 *
	 * @param event The event to check
	 * @return If anything belongs to the provided event
	 */
	public final boolean belongsTo(IReplyCallback event) {
		return switch (event) {
			case SlashCommandInteractionEvent slashEvent -> belongsTo(slashEvent) != null;
			case ButtonInteractionEvent buttonEvent -> belongsTo(buttonEvent) != null;
			case ModalInteractionEvent modalEvent -> belongsTo(modalEvent) != null;
			case GenericSelectMenuInteractionEvent<?, ?> selectionEvent ->
					belongsTo(selectionEvent) != null;
			default -> false;
		};
	}

	/**
	 * The code to execute when the action is ran. This should always acknowledge the event.
	 *
	 * @param event  A reply-able event representing the context that triggered the action
	 * @param args   Any arguments given when upon invocation of this action
	 */
	protected abstract void execute(IReplyCallback event, Node<K, Object> args);

	private void runChecks(IReplyCallback event) throws CheckFailedException {
		for (Check check : checks) {
			check.check(event);
		}
	}

	/**
	 * Runs the action.
	 * <p>
	 * Running the command consists of 2 steps: Checking & Executing.
	 * Checking checks if the action can be executed in the invoked context,
	 * and Executing executes the action if the checking process did not throw a
	 * {@link CheckFailedException}.
	 * </p>
	 *
	 * @param event  The {@link IReplyCallback} containing the command's invocation context
	 */
	public final void run(
			IReplyCallback event
	) {
		try {
			runChecks(event);
			Node<K, Object> args = switch (event) {
				case SlashCommandInteractionEvent slashEvent -> {
					ActionCommand<K> command = belongsTo(slashEvent);
					yield command == null
							? new Node<>()
							: command.getSlashArgs(slashEvent);
				}
				case ButtonInteractionEvent buttonEvent -> {
					ActionButton<K> button = belongsTo(buttonEvent);
					yield button == null
							? new Node<>()
							: button.createArgs(buttonEvent);
				}
				case ModalInteractionEvent modalEvent -> {
					ActionModal<K> modal = belongsTo(modalEvent);
					yield modal == null
							? new Node<>()
							: modal.getModalArgs(modalEvent);
				}
				case GenericSelectMenuInteractionEvent<?, ?> selectionEvent -> {
					ActionSelection<K> selection = belongsTo(selectionEvent);
					yield selection == null
							? new Node<>()
							: selection.getSelectionArgs(selectionEvent);
				}
				default -> new Node<>();
			};
			execute(event, args);
		} catch (CheckFailedException cfe) {
			if (event.isAcknowledged()) {
				event.getHook().sendMessage(cfe.getMessage()).queue();
			} else {
				event.reply(cfe.getMessage()).setEphemeral(true).queue();
			}
		} catch (Exception e) {
			String message = "The bot ran into an internal error, "
					+ String.format("please report this issue to the bot owner (%s)",
					e.getMessage()
			);
			LOGGER.warn(message, e);
			if (event.isAcknowledged()) {
				event.getHook().sendMessage(message).queue();
			} else {
				event.reply(message).setEphemeral(true).queue();
			}
		}
	}

	/**
	 * A generic interface for argument keys. Primarily exists so extending classes can
	 * create an enum of keys, and have the enum class implement this.
	 */
	public interface ArgKey {
		default String str() {
			return this.toString().replace("_", "").toLowerCase();
		}
	}
}
