package no.smileyface.discordbotframework.entities.context;

import java.util.List;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import no.smileyface.discordbotframework.entities.ContextAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.generic.GenericModal;

/**
 * A submittable modal window for executing context actions.
 * {@inheritDoc}
 *
 * @see no.smileyface.discordbotframework.entities.noncontext.ActionModal ActionModal
 */
public class ContextModal<K extends GenericBotAction.ArgKey> extends GenericModal<K> {
	private static final String TYPE_STRING = "mdl";

	private static long idCounter = 0;

	public ContextModal(String title, List<ItemComponent> components) {
		super(nextId(), title, components);
	}

	private static synchronized String nextId() {
		return ContextAction.CONTEXT_PREFIX + TYPE_STRING + idCounter++;
	}
}
