package no.smileyface.discordbotframework.entities;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.context.ContextButton;
import no.smileyface.discordbotframework.entities.context.ContextModal;
import no.smileyface.discordbotframework.entities.context.ContextSelection;
import no.smileyface.discordbotframework.entities.generic.GenericCommand;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a temporary action.</p>
 * <p>To invoke the action, forms of user input can be added to this action:</p>
 * <ul>
 *     <li>Buttons: Added by calling {@link #addButtons(ContextButton...)}</li>
 *     <li>Modals: Added by calling {@link #addModals(ContextModal...)}</li>
 *     <li>Selections: Added by calling {@link #addSelections(ContextSelection...)}</li>
 * </ul>
 *
 * @param <K> Key type used for args given to {@link #execute(IReplyCallback, Node)}.
 */
public abstract non-sealed class ContextAction<K extends GenericBotAction.ArgKey>
		extends GenericBotAction<K, GenericCommand<K>, ContextButton<K>,
		ContextModal<K>, ContextSelection<K>> {
	public static final String CONTEXT_PREFIX = "--ctx";

	private final Duration duration;
	private final BiPredicate<ContextAction<K>, Integer> deactivatePredicate;

	private int runCounter;


	/**
	 * Creates the action.
	 * To invoke the action, action inputs need to be provided from the following methods:
	 *
	 *
	 * @param manager The {@link ActionManager} for this bot
	 * @param expiresAfter How long it takes before this context action expires
	 * @param deactivateAfterUse Is checked after this action has been run,
	 *                           and deactivates the action if this predicate returns {@code true}.
	 *                           The predicate takes the action itself, and a number that represents
	 *                           how many times the action has been run.
	 * @param checks  Any {@link Check}s to set conditions that need to be met
	 *                for the action to go through
	 */
	protected ContextAction(
			ActionManager manager,
			@NotNull Duration expiresAfter,
			BiPredicate<ContextAction<K>, Integer> deactivateAfterUse,
			Check... checks
	) {
		super(manager, checks);
		this.duration = expiresAfter;
		this.deactivatePredicate = deactivateAfterUse;
		this.runCounter = 0;
		getManager().addContextAction(this);
	}

	/**
	 * Creates the action.
	 *
	 * @param manager The {@link ActionManager} for this bot
	 * @param expiresAfter How long it takes before this context action expires
	 * @param deactivateAfterUse If the action should be deactivated after it's run
	 * @param checks  Any {@link Check}s to set conditions that need to be met
	 *                for the action to go through
	 */
	protected ContextAction(
			ActionManager manager,
			@NotNull Duration expiresAfter,
			boolean deactivateAfterUse,
			Check... checks
	) {
		this(manager, expiresAfter, (action, count) -> deactivateAfterUse, checks);
	}

	/**
	 * Creates the action.
	 *
	 * @param manager The {@link ActionManager} for this bot
	 * @param expiresAfter How long it takes before this context action expires
	 * @param useTimes How many times the action can be used before it is deactivated
	 *                 Any value of 1 or less will deactivate the action on first use
	 * @param checks  Any {@link Check}s to set conditions that need to be met
	 *                for the action to go through
	 */
	protected ContextAction(
			ActionManager manager,
			@NotNull Duration expiresAfter,
			int useTimes,
			Check... checks
	) {
		this(manager, expiresAfter, (action, count) -> useTimes <= count, checks);
	}

	/**
	 * Schedules something for when this context action expires.
	 *
	 * @param scheduler The scheduler to schedule this on
	 * @param onExpiry  What to do when this action expires
	 * @return The scheduled task
	 */
	public final ScheduledFuture<?> scheduleOnExpiry(
			ScheduledExecutorService scheduler,
			Runnable onExpiry
	) {
		return scheduler.schedule(onExpiry, duration.getSeconds(), TimeUnit.SECONDS);
	}

	public final boolean checkDeactivate() {
		return deactivatePredicate.test(this, ++runCounter);
	}
}
