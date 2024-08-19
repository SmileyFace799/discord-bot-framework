package no.smileyface.discordbotframework;

import java.util.Collection;
import no.smileyface.discordbotframework.entities.BotAction;
import org.jetbrains.annotations.NotNull;

/**
 * <p>An interface to help creating actions. Usage of this is optional</p>
 * <p>This can be implemented however preferred for more advanced action initialization, but
 * {@link #createActions(ActionManager)} should always return a collection of
 * every action the bot has.</p>
 */
public interface ActionInitializer {
	/**
	 * Initializes bot actions.
	 * Should any extra arguments be desired, implement a constructor to set them
	 *
	 * @param manager The {@link ActionManager} of the bot
	 * @return A list of every action the bot has
	 */
	@NotNull
	Collection<? extends BotAction<? extends BotAction.ArgKey>> createActions(
			ActionManager manager
	);
}
