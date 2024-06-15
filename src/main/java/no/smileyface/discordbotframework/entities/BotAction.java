package no.smileyface.discordbotframework.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.checks.ChecksFailedException;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Represents a basic bot action. Actions can be triggered through slash commands, buttons & modals.
 *
 * @param <K> Key type used for args given to
 *            {@link #execute(IReplyCallback, MultiTypeMap, InputRecord)}.
 */
public abstract class BotAction<K extends BotAction.ArgKey> {
	private final Collection<ActionCommand<K>> commands;
	private final Collection<ActionButton<K>> buttons;
	private final Collection<ActionModal<K>> modals;
	private final Collection<Check> checks;

	protected BotAction(
			Collection<ActionCommand<K>> commands,
			Collection<ActionButton<K>> buttons,
			Collection<ActionModal<K>> modals,
			Check... checks
	) {
		this.commands = commands == null ? Set.of() : commands;
		this.buttons = buttons == null ? Set.of() : buttons;
		this.modals = modals == null ? Set.of() : modals;
		this.checks = checks == null ? Set.of() : Arrays.stream(checks).toList();
	}

	protected BotAction(
			ActionCommand<K> command,
			ActionButton<K> button,
			ActionModal<K> modal,
			Check... checks
	) {
		this(Set.of(command), Set.of(button), Set.of(modal), checks);
	}

	protected BotAction(ActionCommand<K> command, Check... checks) {
		this(command, null, null, checks);
	}

	protected BotAction(ActionButton<K> button, Check... checks) {
		this(null, button, null, checks);
	}

	protected BotAction(ActionModal<K> modal, Check... checks) {
		this(null, null, modal, checks);
	}

	protected BotAction(ActionCommand<K> command, ActionButton<K> button, Check... checks) {
		this(command, button, null, checks);
	}

	protected BotAction(ActionCommand<K> command, ActionModal<K> modal, Check... checks) {
		this(command, null, modal, checks);
	}

	protected BotAction(ActionButton<K> button, ActionModal<K> modal, Check... checks) {
		this(null, button, modal, checks);
	}

	public Collection<ActionCommand<K>> getCommands() {
		return commands;
	}

	public Collection<ActionButton<K>> getButtons() {
		return buttons;
	}

	public Collection<ActionModal<K>> getModals() {
		return modals;
	}

	/**
	 * The code to execute when the action is ran. This should always acknowledge the event.
	 *
	 * @param event  A reply-able event representing the context that triggered the action
	 * @param args   Any arguments given when upon invocation of this action
	 * @param inputs All registered inputs
	 */
	protected abstract void execute(IReplyCallback event, MultiTypeMap<K> args, InputRecord inputs);

	private void runChecks(IReplyCallback event) throws ChecksFailedException {
		for (Check check : checks) {
			check.check(event);
		}
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
			default -> false;
		};
	}

	/**
	 * Runs the action.
	 * <p>
	 * Running the command consists of 2 steps: Checking & Executing.
	 * Checking checks if the action can be executed in the invoked context,
	 * and Executing executes the action if the checking process did not yield any exceptions.
	 * </p>
	 *
	 * @param event  The {@link IReplyCallback} containing the command's invocation context
	 * @param inputs Every registered input in the bot
	 */
	public final void run(
			IReplyCallback event,
			InputRecord inputs
	) {
		try {
			runChecks(event);
			MultiTypeMap<K> args = switch (event) {
				case SlashCommandInteractionEvent slashEvent -> {
					ActionCommand<K> command = belongsTo(slashEvent);
					yield command == null
							? new MultiTypeMap<>()
							: belongsTo(slashEvent).getSlashArgs(slashEvent);
				}
				case ButtonInteractionEvent buttonEvent -> {
					ActionButton<K> button = belongsTo(buttonEvent);
					yield button == null
							? new MultiTypeMap<>()
							: belongsTo(buttonEvent).createArgs(buttonEvent);
				}
				case ModalInteractionEvent modalEvent -> {
					ActionModal<K> modal = belongsTo(modalEvent);
					yield modal == null
							? new MultiTypeMap<>()
							: belongsTo(modalEvent).getModalArgs(modalEvent);
				}
				default -> new MultiTypeMap<>();
			};
			execute(event, args, inputs);
		} catch (ChecksFailedException cfe) {
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
			if (event.isAcknowledged()) {
				event.getHook().sendMessage(message).queue();
			} else {
				event.reply(message).setEphemeral(true).queue();
			}
		}
	}

	/**
	 * A generic interface for slash arg keys. Primarily exists so extending classes can
	 * create an enum of keys, and have the enum class implement this.
	 */
	public interface ArgKey {
		default String str() {
			return this.toString().replace("_", "").toLowerCase();
		}
	}
}
