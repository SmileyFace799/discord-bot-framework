package no.smileyface.discordbotframework.entities;

import java.util.Collection;
import java.util.Set;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.generic.GenericModal;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;
import no.smileyface.discordbotframework.entities.noncontext.ActionModal;
import no.smileyface.discordbotframework.entities.noncontext.ActionSelection;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a basic bot action.</p>
 * <p>To invoke the action, forms of user input can be added to this action:</p>
 * <ul>
 *     <li>Commands: Must be provided in a constructor</li>
 *     <li>Buttons: Added by calling {@link #addButtons(ActionButton...)}</li>
 *     <li>Modals: Added by calling {@link #addModals(ActionModal...)}</li>
 *     <li>Selections: Added by calling {@link #addSelections(ActionSelection...)}</li>
 * </ul>
 *
 * @param <K> Key type used for args given to {@link #execute(IReplyCallback, Node)}.
 */
public abstract non-sealed class BotAction<K extends GenericBotAction.ArgKey>
		extends GenericBotAction<K, ActionCommand<K>, ActionButton<K>,
		ActionModal<K>, ActionSelection<K>> {
	protected BotAction(
			ActionManager manager,
			@NotNull Collection<ActionCommand<K>> actionCommands,
			Check... checks
	) {
		super(manager, actionCommands, checks);
	}

	protected BotAction(
			ActionManager manager,
			@NotNull ActionCommand<K> command,
			Check... checks
	) {
		super(manager, command, checks);
	}

	protected BotAction(ActionManager manager, Check... checks) {
		super(manager, checks);
	}

	/**
	 * <p>Creates an actions that just responds to any incoming events with a modal.</p>
	 * <p><b>NB: This action cannot respond with a modal if it was triggered by a modal.
	 * Discord does not allow modal chaining.</b></p>
	 *
	 * @param responseModal The modal to respond to any incoming events with
	 * @param manager       Same as constructor
	 * @param commands      Same as constructor
	 * @param checks        Same as constructor
	 * @return A new bot action that will respond to any incoming events with the provided modal
	 * @see #BotAction(ActionManager, Collection, Check...)
	 */
	public static BotAction<ArgKey> respondWithModal(
			GenericModal<?> responseModal,
			ActionManager manager,
			@NotNull Collection<ActionCommand<ArgKey>> commands,
			Check... checks
	) {
		return new BotAction<>(manager, commands, checks) {
			@Override
			protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
				if (event instanceof IModalCallback modalCallback) {
					modalCallback.replyModal(responseModal).queue();
				} else {
					event.reply("Could not show modal window: "
							+ "Event does not allow modal responses"
					).setEphemeral(true).queue();
				}
			}
		};
	}

	/**
	 * <p>Creates an actions that just responds to any incoming events with a modal.</p>
	 * <p><b>NB: This action cannot respond with a modal if it was triggered by a modal.
	 * Discord does not allow modal chaining.</b></p>
	 *
	 * @param responseModal The modal to respond to any incoming events with
	 * @param manager       Same as constructor
	 * @param command       Same as constructor
	 * @param checks        Same as constructor
	 * @return A new bot action, that will respond to any incoming events with the provided modal
	 * @see #BotAction(ActionManager, ActionCommand, Check...)
	 */
	public static BotAction<ArgKey> respondWithModal(
			GenericModal<?> responseModal,
			ActionManager manager,
			@NotNull ActionCommand<ArgKey> command,
			Check... checks
	) {
		return respondWithModal(responseModal, manager, Set.of(command), checks);
	}

	/**
	 * <p>Creates an actions that just responds to any incoming events with a modal.</p>
	 * <p><b>NB: This action cannot respond with a modal if it was triggered by a modal.
	 * Discord does not allow modal chaining.</b></p>
	 *
	 * @param responseModal The modal to respond to any incoming events with
	 * @param manager       Same as constructor
	 * @param checks        Same as constructor
	 * @return A new bot action, that will respond to any incoming events with the provided modal
	 * @see #BotAction(ActionManager, Check...)
	 */
	public static BotAction<ArgKey> respondWithModal(
			GenericModal<?> responseModal,
			ActionManager manager,
			Check... checks
	) {
		return respondWithModal(responseModal, manager, Set.of(), checks);
	}
}
