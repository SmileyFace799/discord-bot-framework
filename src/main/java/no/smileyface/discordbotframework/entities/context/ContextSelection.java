package no.smileyface.discordbotframework.entities.context;

import java.util.function.Function;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import no.smileyface.discordbotframework.entities.ActionSelection;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * A selection menu for executing actions by selecting something.
 * {@inheritDoc}
 */
public class ContextSelection<K extends BotAction.ArgKey> extends ActionSelection<K> {
	private static final String TYPE_STRING = "sct";

	private static long idCounter = 0;

	/**
	 * Creates a context selection.
	 *
	 * @param builderFunction A function to create & modify a selection builder.
	 *                        The string provided is the selection's context ID,
	 *                        and should be used as the selection ID
	 *                        (It will be set to this value before being built regardless)
	 * @param nextValueKey    The key to use for the next selected value
	 *                        in the value node provided by
	 *                        {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}
	 */
	public ContextSelection(
			Function<String, SelectMenu.Builder<?, ?>> builderFunction,
			K nextValueKey
	) {
		super(makeSelection(builderFunction), nextValueKey);
	}

	private static SelectMenu makeSelection(Function<String, SelectMenu.Builder<?, ?>> builder) {
		String id = nextId();
		return builder.apply(id).setId(id).build();
	}

	private static synchronized String nextId() {
		return ContextAction.CONTEXT_PREFIX + TYPE_STRING + idCounter++;
	}
}