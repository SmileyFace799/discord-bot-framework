package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

/**
 * A button for executing actions.
 */
public class ActionButton extends ButtonImpl {
	public ActionButton(ButtonStyle style, String id, String text) {
		this(style, id, text, null);
	}

	public ActionButton(ButtonStyle style, String id, String text, Emoji emoji) {
		super(id, text, style, false, emoji);
	}
}
