package no.smileyface.discordbotframework;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.context.ContextAction;
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

	private final ScheduledExecutorService scheduler;
	private final Map<ContextAction<? extends BotAction.ArgKey>, ScheduledFuture<?>>
			contextActionRemovalTasks;
	private final Collection<BotAction<? extends BotAction.ArgKey>> actions;
	private final Identifier identifier;
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
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.contextActionRemovalTasks = new HashMap<>();
		this.actions = new HashSet<>(actionInitializer.createActions(this));
		this.identifier = new Identifier(actions);
		this.defaultNotFoundMessage = "Oops, the bot doesn't know how to respond to "
				+ "whatever you just did. Please contact the bot owner";
	}

	public Identifier getIdentifier() {
		return identifier;
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
	 * Adds a context action.
	 *
	 * @param action The context action to add
	 * @throws IllegalArgumentException If the context action is already added
	 */
	public final void addContextAction(ContextAction<? extends BotAction.ArgKey> action) {
		if (actions.contains(action)) {
			throw new IllegalArgumentException("This action is already added");
		}
		this.actions.add(action);
		this.contextActionRemovalTasks.put(
				action,
				action.scheduleOnExpiry(scheduler, () -> {
					synchronized (actions) {
						actions.remove(action);
					}
				})
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

	/**
	 * Runs an action belonging to an event.
	 *
	 * @param event The event received
	 * @param identifiableId The ID of the identifiable that triggered the event
	 */
	protected final void onActionEvent(IReplyCallback event, String identifiableId) {
		identifier.findAction(event).ifPresentOrElse(
				action -> {
					action.run(event);
					if (action instanceof ContextAction<?> contextAction
							&& contextAction.checkDeactivate()) {
						contextActionRemovalTasks.remove(action).cancel(false);
						synchronized (actions) {
							actions.remove(action);
						}
					}
				},
				() -> event.reply(identifiableId.startsWith(ContextAction.CONTEXT_PREFIX)
						? "This action has expired"
						: defaultNotFoundMessage
				).setEphemeral(true).queue()
		);
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
		onActionEvent(event, event.getName());
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		onActionEvent(event, event.getComponentId());
	}

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		onActionEvent(event, event.getModalId());
	}

	@Override
	public void onGenericSelectMenuInteraction(@NotNull GenericSelectMenuInteractionEvent event) {
		onActionEvent(event, event.getComponentId());
	}
}
