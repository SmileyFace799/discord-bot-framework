package no.smileyface.discordbotframework.entities.context;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import org.jetbrains.annotations.NotNull;

/**
 * A temporary action that can be created after the bot has started.
 * Cannot be invoked by commands, as commands are registered on startup.
 * To invoke the action, action inputs need to be provided from the following methods:
 * <ul>
 *     <li>{@link #createButtons()}: Buttons that should trigger this context action</li>
 *     <li>{@link #createModals()}: Modals that should trigger this context action
 *     when submitted</li>
 *     <li>{@link #createSelections()}: Selections that should trigger this context action
 *     when submitted</li>
 * </ul>
 *
 * @param <K> Key type used for args given to {@link #execute(
 * net.dv8tion.jda.api.interactions.callbacks.IReplyCallback,
 * no.smileyface.discordbotframework.data.Node) #execute(IReplyCallback, Node)}
 */
public abstract class ContextAction<K extends BotAction.ArgKey> extends BotAction<K> {
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
		return scheduler.schedule(onExpiry, duration.time(), duration.unit());
	}

	public final boolean checkDeactivate() {
		return deactivatePredicate.test(this, ++runCounter);
	}

	/**
	 * As commands have to be initialized on startup, context actions cannot have them.
	 *
	 * @return An empty collection of commands
	 */
	@NotNull
	@Override
	protected final Collection<? extends ActionCommand<K>> createCommands() {
		return super.createCommands();
	}

	@NotNull
	@Override
	protected Collection<? extends ContextButton<K>> createButtons() {
		return Set.of();
	}

	@NotNull
	@Override
	protected Collection<? extends ContextModal<K>> createModals() {
		return Set.of();
	}

	@NotNull
	@Override
	protected Collection<? extends ContextSelection<K>> createSelections() {
		return Set.of();
	}

	/**
	 * Represents a duration for how long the context action will last.
	 *
	 * @param time The amount of time it lasts
	 * @param unit The unit of time that {@code amount} is specified in
	 */
	public record Duration(long time, TimeUnit unit) {}
}
