package no.smileyface.discordbotframework.entities;

import java.util.Collection;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.data.Node;

/**
 * A button for executing context actions, that depend on a different action for their execution.
 */
public abstract class ContextButton<K extends BotAction.ArgKey> extends ActionButton<K> {
	private static long nextId = 0;

	private static String nextId() {
		long next = nextId;
		nextId++;
		return "--ctxButton" + next;
	}

	protected ContextButton(BotAction<K> action, ButtonStyle style, String text) {
		super(style, nextId(), text);
		action.registerContextButton(this);
	}

	protected ContextButton(BotAction<K> action, ButtonStyle style, String text, Emoji emoji) {
		super(style, nextId(), text, emoji);
		action.registerContextButton(this);
	}

	public abstract void clicked(
			ButtonInteractionEvent event,
			Node<K, Object> args
	);

	final synchronized void clickedThenDelete(
			ButtonInteractionEvent event,
			Node<K, Object> args,
			Collection<ActionButton<K>> buttonList
	) {
		clicked(event, args);
		buttonList.remove(this);
	}
}
