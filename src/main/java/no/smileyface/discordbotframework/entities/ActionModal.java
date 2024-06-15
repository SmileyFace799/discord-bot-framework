package no.smileyface.discordbotframework.entities;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.internal.interactions.modal.ModalImpl;
import no.smileyface.discordbotframework.misc.Identifiable;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * A submittable modal window for executing actions.
 *
 * @param <K> Key type used for args returned from {@link #getModalArgs(ModalInteractionEvent)}.
 */
public class ActionModal<K extends BotAction.ArgKey> extends ModalImpl implements Identifiable {

	public ActionModal(String id, String title, List<ItemComponent> components) {
		super(id, title, components.stream().map(item ->
				(LayoutComponent) ActionRow.of(item)).toList());
	}

	/**
	 * Creates action arguments from a modal interaction event.
	 * <p>This returns an empty map by default.</p>
	 *
	 * @param event The modal event representing the submitted modal
	 * @return Map of action arguments
	 */
	public MultiTypeMap<K> getModalArgs(ModalInteractionEvent event) {
		return new MultiTypeMap<>();
	}

	@Override
	public boolean identify(String id) {
		return getId().equalsIgnoreCase(id);
	}
}
