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
 * 			  {@link #execute(IReplyCallback, MultiTypeMap, InputRecord)}.
 */
public abstract class BotAction<K extends BotAction.ArgKey> {
    private final ActionCommand<K> command;
    private final ActionButton<K> button;
    private final ActionModal<K> modal;
    private final Collection<Check> checks;

    protected BotAction(
            ActionCommand<K> command,
            ActionButton<K> button,
            ActionModal<K> modal,
            Check... checks
    ) {
        this.command = command;
        this.button = button;
        this.modal = modal;
        this.checks = checks == null ? Set.of() : Arrays.stream(checks).toList();
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

    public final ActionCommand<K> getCommand() {
        return command;
    }

    public final ActionButton<K> getButton() {
        return button;
    }

    public final ActionModal<K> getModal() {
        return modal;
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

	/**
	 * Determines if this action belongs to a specified event.
	 *
	 * @param event The event to check if this action belongs to or not
	 * @return If this action belongs to the specified event
	 */
	public final boolean belongsTo(IReplyCallback event) {
		return switch (event) {
			case SlashCommandInteractionEvent slashEvent -> command != null && command
					.hasVariant(slashEvent.getName());
			case ModalInteractionEvent modalEvent -> modal != null && modal.getId()
					.equalsIgnoreCase(modalEvent.getModalId());
			case ButtonInteractionEvent buttonEvent -> button != null && button.getId() != null
					&& button.getId().equalsIgnoreCase(buttonEvent.getComponentId());
			default -> false;
		};
	}

    /**
     * Runs the action.
     * <p>
     *     Running the command consists of 2 steps: Checking & Executing.
     *     Checking checks if the action can be executed in the invoked context,
     *     and Executing executes the action if the checking process did not yield any exceptions.
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
                case SlashCommandInteractionEvent slashEvent -> command == null
						? new MultiTypeMap<>()
						: command.getSlashArgs(slashEvent);
                case ModalInteractionEvent modalEvent -> modal == null ? new MultiTypeMap<>()
						: modal.getModalArgs(modalEvent);
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
