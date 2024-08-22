package no.smileyface.discordbotframework.entities;

import java.util.Collection;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.data.Node;
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


}
