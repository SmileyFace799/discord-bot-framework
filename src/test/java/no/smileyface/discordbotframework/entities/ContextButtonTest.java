package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.data.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextButtonTest {
	private BotAction<BotAction.ArgKey> testAction;
	private ContextButton<BotAction.ArgKey> contextButton;

	@BeforeEach
	public void beforeEach() {
		testAction = new BotAction<>(null) {
			@Override
			protected void execute(
					IReplyCallback event,
					Node<ArgKey, Object> args
			) {
				// Do nothing
			}
		};
		contextButton = new ContextButton<>(testAction, ButtonStyle.PRIMARY, "test") {
			@Override
			public void clicked(
					ButtonInteractionEvent event,
					Node<BotAction.ArgKey, Object> args
			) {
				// Do nothing
			}
		};
	}

	@Test
	void testContextButtonExecutesThenDeletes() {
		IReplyCallback testEvent = MockEventFactory.makeButtonEvent(contextButton.getId());
		assertTrue(testAction.belongsTo(testEvent));
		testAction.run(testEvent);
		assertFalse(testAction.belongsTo(testEvent));
	}
}
