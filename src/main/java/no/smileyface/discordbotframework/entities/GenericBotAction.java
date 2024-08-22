package no.smileyface.discordbotframework.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import no.smileyface.discordbotframework.entities.generic.GenericButton;
import no.smileyface.discordbotframework.entities.generic.GenericCommand;
import no.smileyface.discordbotframework.entities.generic.GenericModal;
import no.smileyface.discordbotframework.entities.generic.GenericSelection;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Represents a basic bot action.</p>
 * <p>To invoke the action, forms of user input can be added to this action:</p>
 * <ul>
 *     <li>Commands: Must be provided in a constructor</li>
 *     <li>Buttons: Added by calling {@link #addButtons(GenericButton...)}</li>
 *     <li>Modals: Added by calling {@link #addModals(GenericModal...)}</li>
 *     <li>Selections: Added by calling {@link #addSelections(GenericSelection...)}</li>
 * </ul>
 *
 * @param <K> Key type used for args given to {@link #execute(IReplyCallback, Node)}.
 */
public abstract sealed class GenericBotAction<
		K extends GenericBotAction.ArgKey,
		C extends GenericCommand<K>,
		B extends GenericButton<K>,
		M extends GenericModal<K>,
		S extends GenericSelection<K>>
		permits BotAction, ContextAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericBotAction.class);

	private final ActionManager manager;
	private final Collection<C> commands;
	private final Collection<B> buttons;
	private final Collection<M> modals;
	private final Collection<S> selections;
	private final Collection<Check> checks;

	/**
	 * Creates the action.
	 *
	 * @param manager  The {@link ActionManager} for this bot
	 * @param commands A collection of commands that can invoke the action.
	 *                 Commands are "set in stone" once the bot starts,
	 *                 and must therefore be provided in the constructor,
	 *                 unlike other options for user input
	 * @param checks   Any {@link Check}s to set conditions that need to be met
	 *                 for the action to go through
	 * @see #addButtons(GenericButton...)
	 * @see #addModals(GenericModal...)
	 * @see #addSelections(GenericSelection...)
	 */
	protected GenericBotAction(
			ActionManager manager,
			@NotNull Collection<C> commands,
			Check... checks
	) {
		this.manager = manager;
		// Commands should never change after initialization, and should therefore be immutables
		this.commands = Collections.unmodifiableCollection(commands);
		this.buttons = new HashSet<>();
		this.modals = new HashSet<>();
		this.selections = new HashSet<>();
		this.checks = checks == null ? List.of() : List.of(checks);
	}

	/**
	 * Creates the action.
	 *
	 * @param manager The {@link ActionManager} for this bot
	 * @param command A command that can invoke the action.
	 *                Commands are "set in stone" once the bot starts,
	 *                and must therefore be provided in the constructor,
	 *                unlike other options for user input
	 * @param checks  Any {@link Check}s to set conditions that need to be met
	 *                for the action to go through
	 * @see #addButtons(GenericButton...)
	 * @see #addModals(GenericModal...)
	 * @see #addSelections(GenericSelection...)
	 */
	protected GenericBotAction(ActionManager manager, @NotNull C command, Check... checks) {
		this(manager, Set.of(command), checks);
	}

	/**
	 * Creates the action with no commands.
	 *
	 * @param manager The {@link ActionManager} for this bot
	 * @param checks  Any {@link Check}s to set conditions that need to be met
	 *                for the action to go through
	 * @see #addButtons(GenericButton...)
	 * @see #addModals(GenericModal...)
	 * @see #addSelections(GenericSelection...)
	 */
	protected GenericBotAction(ActionManager manager, Check... checks) {
		this(manager, Set.of(), checks);
	}

	protected final ActionManager getManager() {
		return manager;
	}

	protected final Identifier getIdentifier() {
		return manager.getIdentifier();
	}

	public final Collection<C> getCommands() {
		return commands;
	}

	public final Collection<B> getButtons() {
		return Collections.unmodifiableCollection(buttons);
	}

	public final Collection<M> getModals() {
		return Collections.unmodifiableCollection(modals);
	}

	public final Collection<S> getSelections() {
		return Collections.unmodifiableCollection(selections);
	}

	@SafeVarargs
	public final void addButtons(B... buttons) {
		this.buttons.addAll(Set.of(buttons));
	}

	@SafeVarargs
	public final void addModals(M... modals) {
		this.modals.addAll(Set.of(modals));
	}

	@SafeVarargs
	public final void addSelections(S... selections) {
		this.selections.addAll(Set.of(selections));
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
				case SlashCommandInteractionEvent slashEvent -> Identifier
						.identify(commands, slashEvent.getName())
						.map(command -> command.getSlashArgs(slashEvent))
						.orElse(new Node<>());
				case ButtonInteractionEvent buttonEvent -> Identifier
						.identify(buttons, buttonEvent.getComponentId())
						.map(button -> button.createArgs(buttonEvent))
						.orElse(new Node<>());
				case ModalInteractionEvent modalEvent -> Identifier
						.identify(modals, modalEvent.getModalId())
						.map(modal -> modal.getModalArgs(modalEvent))
						.orElse(new Node<>());
				case GenericSelectMenuInteractionEvent<?, ?> selectionEvent -> Identifier
						.identify(selections, selectionEvent.getComponentId())
						.map(selection -> selection.getSelectionArgs(selectionEvent))
						.orElse(new Node<>());
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
