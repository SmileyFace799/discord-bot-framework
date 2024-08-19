package no.smileyface.discordbotframework.entities.context;

import java.util.List;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import no.smileyface.discordbotframework.entities.ActionModal;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * A submittable modal window for executing context actions.
 * {@inheritDoc}
 */
public class ContextModal<K extends BotAction.ArgKey> extends ActionModal<K> {
	private static final String TYPE_STRING = "mdl";

	private static long idCounter = 0;

	public ContextModal(String title, List<ItemComponent> components) {
		super(nextId(), title, components);
	}

	private static synchronized String nextId() {
		return ContextAction.CONTEXT_PREFIX + TYPE_STRING + idCounter++;
	}
}
