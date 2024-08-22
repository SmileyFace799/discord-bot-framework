package no.smileyface.discordbotframework.entities.noncontext;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.generic.GenericButton;
import org.jetbrains.annotations.NotNull;

/**
 * A button for executing regular bot actions.
 * {@inheritDoc}
 *
 * @see no.smileyface.discordbotframework.entities.context.ContextButton ContextButton
 */
public class ActionButton<K extends GenericBotAction.ArgKey> extends GenericButton<K> {
	/**
	 * Creates a non-anonymous action button.
	 *
	 * @param style The button's {@link ButtonStyle style}
	 * @param id The button's ID
	 * @param text The text shown on the button when it's displayed
	 * @param emoji The emoji shown on the button when it's displayed
	 */
	public ActionButton(@NotNull ButtonStyle style, @NotNull String id, String text, Emoji emoji) {
		super(style, id, text, emoji);
	}

	/**
	 * Creates a non-anonymous action button.
	 *
	 * @param style The button's {@link ButtonStyle style}
	 * @param id The button's ID
	 * @param text The text shown on the button when it's displayed
	 */
	public ActionButton(@NotNull ButtonStyle style, @NotNull String id, String text) {
		this(style, id, text, null);
	}
}
