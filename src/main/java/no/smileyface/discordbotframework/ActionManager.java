package no.smileyface.discordbotframework;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import no.smileyface.discordbotframework.entities.BotAction;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for events & runs corresponding actions.
 * See below for which {@link ListenerAdapter} methods are implemented
 *
 * @see #onReady(ReadyEvent)
 * @see #onSlashCommandInteraction(SlashCommandInteractionEvent)
 * @see #onButtonInteraction(ButtonInteractionEvent)
 * @see #onModalInteraction(ModalInteractionEvent)
 */
public class ActionManager extends ListenerAdapter {
	private final Collection<? extends BotAction<? extends BotAction.ArgKey>> actions;
	private final InputRecord inputs;

	/**
	 * Constructor.
	 *
	 * @param actions The collection of actions that the bot can perform.
	 */
	public ActionManager(Collection<? extends BotAction<? extends BotAction.ArgKey>> actions) {
		this.actions = actions;
		this.inputs = new InputRecord(actions);
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
		Logger.getLogger(getClass().getName()).log(
				Level.INFO, "{0} is ready", event.getJDA().getSelfUser().getName()
		);
	}

	private void onActionEvent(IReplyCallback event) {
		actions.stream().filter(action -> action.belongsTo(event)).findFirst().ifPresentOrElse(
				action -> action.run(event, inputs),
				() -> event.reply("Oops, the bot doesn't know how to respond to "
						+ "whatever you just did. Please contact the bot owner").queue()
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
		onActionEvent(event);
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		onActionEvent(event);
	}

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		onActionEvent(event);
	}
}
