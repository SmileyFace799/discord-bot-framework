package no.smileyface.discordbotframework.entities.context;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.entities.ContextAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.generic.GenericButton;
import org.jetbrains.annotations.NotNull;

/**
 * A button for executing context actions.
 * {@inheritDoc}
 *
 * @see no.smileyface.discordbotframework.entities.noncontext.ActionButton ActionButton
 */
public class ContextButton<K extends GenericBotAction.ArgKey> extends GenericButton<K> {
	private static final String TYPE_STRING = "btn";

	private static long idCounter = 0;

	/**
	 * Creates an anonymous action button.
	 *
	 * @param style The button's {@link ButtonStyle style}
	 * @param text The text shown on the button when it's displayed
	 * @param emoji The emoji shown on the button when it's displayed
	 */
	public ContextButton(@NotNull ButtonStyle style, String text, Emoji emoji) {
		super(style, nextId(), text, emoji);
	}

	/**
	 * Creates an anonymous action button.
	 *
	 * @param style The button's {@link ButtonStyle style}
	 * @param text The text shown on the button when it's displayed
	 */
	public ContextButton(@NotNull ButtonStyle style, String text) {
		this(style, text, null);
	}

	private static synchronized String nextId() {
		return ContextAction.CONTEXT_PREFIX + TYPE_STRING + idCounter++;
	}
}
