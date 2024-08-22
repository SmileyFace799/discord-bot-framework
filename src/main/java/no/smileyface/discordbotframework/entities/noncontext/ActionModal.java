package no.smileyface.discordbotframework.entities.noncontext;

import java.util.List;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.generic.GenericModal;

/**
 * A submittable modal window for executing regular actions.
 * {@inheritDoc}
 *
 * @see no.smileyface.discordbotframework.entities.context.ContextModal ContextModal
 */
public class ActionModal<K extends GenericBotAction.ArgKey> extends GenericModal<K> {

	public ActionModal(String id, String title, List<ItemComponent> components) {
		super(id, title, components);
	}
}
