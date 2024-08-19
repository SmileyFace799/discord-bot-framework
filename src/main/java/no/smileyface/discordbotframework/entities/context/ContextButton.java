package no.smileyface.discordbotframework.entities.context;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * A button for executing context actions.
 * {@inheritDoc}
 */
public class ContextButton<K extends BotAction.ArgKey> extends ActionButton<K> {
	private static final String TYPE_STRING = "btn";

	private static long idCounter = 0;

	public ContextButton(ButtonStyle style, String text) {
		super(style, nextId(), text);
	}

	public ContextButton(ButtonStyle style, String text, Emoji emoji) {
		super(style, nextId(), text, emoji);
	}

	private static synchronized String nextId() {
		return ContextAction.CONTEXT_PREFIX + TYPE_STRING + idCounter++;
	}
}
