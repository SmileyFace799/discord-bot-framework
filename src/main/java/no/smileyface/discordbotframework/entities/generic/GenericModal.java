package no.smileyface.discordbotframework.entities.generic;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.internal.interactions.modal.ModalImpl;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.Identifiable;

/**
 * A submittable modal window for executing actions.
 *
 * @param <K> Key type used for args returned from {@link #getModalArgs(ModalInteractionEvent)}.
 */
public abstract class GenericModal<K extends GenericBotAction.ArgKey>
		extends ModalImpl
		implements Identifiable {
	protected GenericModal(String id, String title, List<ItemComponent> components) {
		super(id, title, components.stream().map(item ->
				(LayoutComponent) ActionRow.of(item)).toList());
	}

	/**
	 * Creates action arguments from a modal interaction event.
	 * <p>This returns an empty node by default.</p>
	 *
	 * @param event The modal event representing the submitted modal
	 * @return Node of action arguments
	 */
	public Node<K, Object> getModalArgs(ModalInteractionEvent event) {
		return new Node<>();
	}

	@Override
	public boolean identify(String id) {
		return getId().equalsIgnoreCase(id);
	}
}
