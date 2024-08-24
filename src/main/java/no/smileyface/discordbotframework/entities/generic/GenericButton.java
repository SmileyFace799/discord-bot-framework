package no.smileyface.discordbotframework.entities.generic;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.Identifiable;

/**
 * A button for executing actions.
 *
 * @param <K> Key type used for args returned from {@link #createArgs(ButtonInteractionEvent)}
 */
public abstract class GenericButton<K extends GenericBotAction.ArgKey>
		extends ButtonImpl
		implements Identifiable {
	protected GenericButton(ButtonStyle style, String id, String label, Emoji emoji) {
		super(id, label, style, false, emoji);
	}

	protected GenericButton(ButtonStyle style, String id, String label) {
		this(style, id, label, null);
	}

	/**
	 * Implementations can override this to fill in default argument to be used
	 * if the action is triggered by a button, as a button event cannot include arguments.
	 *
	 * @return The node of arguments for the associated action to use when executed
	 */
	public Node<K, Object> createArgs(ButtonInteractionEvent event) {
		return new Node<>();
	}

	@Override
	public boolean identify(String id) {
		return getId() != null && getId().equalsIgnoreCase(id);
	}
}
