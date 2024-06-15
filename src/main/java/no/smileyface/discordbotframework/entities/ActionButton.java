package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import no.smileyface.discordbotframework.misc.Identifiable;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * A button for executing actions.
 */
public class ActionButton<K extends BotAction.ArgKey> extends ButtonImpl implements Identifiable {
	public ActionButton(ButtonStyle style, String id, String text) {
		this(style, id, text, null);
	}

	public ActionButton(ButtonStyle style, String id, String text, Emoji emoji) {
		super(id, text, style, false, emoji);
	}

	/**
	 * Implementations can override this to fill in default argument to be used,
	 * if the action is triggered by a button, as a button event cannot include arguments.
	 *
	 * @return The map of arguments for the associated action to use when executed
	 */
	public MultiTypeMap<K> createArgs(ButtonInteractionEvent event) {
		return new MultiTypeMap<>();
	}

	@Override
	public boolean identify(String id) {
		return getId() != null && getId().equalsIgnoreCase(id);
	}
}
