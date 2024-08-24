package no.smileyface.discordbotframework.entities.noncontext;

import java.util.function.Supplier;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.generic.GenericSelection;

/**
 * A selection menu for executing regular actions by selecting something.
 * {@inheritDoc}
 *
 * @see no.smileyface.discordbotframework.entities.context.ContextSelection ContextSelection
 */
public class ActionSelection<K extends GenericBotAction.ArgKey> extends GenericSelection<K> {
	/**
	 * Creates an action selection.
	 *
	 * @param builderSupplier A supplier for the "base" selection menu to use
	 * @param nextValueKey    The key to use for the next selected value in the value node provided
	 *                        by {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}
	 */
	public ActionSelection(Supplier<SelectMenu.Builder<?, ?>> builderSupplier, K nextValueKey) {
		super(builderSupplier, nextValueKey);
	}
}
