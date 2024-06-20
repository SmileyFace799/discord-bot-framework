package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.misc.MultiTypeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextButtonTest {
	private BotAction<BotAction.ArgKey> testAction;
	private ContextButton<BotAction.ArgKey> contextButton;

	@BeforeEach
	public void beforeEach() {
		testAction = new BotAction<>((ActionButton<BotAction.ArgKey>) null) {
			@Override
			protected void execute(
					IReplyCallback event,
					MultiTypeMap<ArgKey> args,
					InputRecord inputs
			) {
				// Do nothing
			}
		};
		contextButton = new ContextButton<>(testAction, ButtonStyle.PRIMARY, "test") {
			@Override
			public void clicked(
					ButtonInteractionEvent event,
					MultiTypeMap<BotAction.ArgKey> args,
					InputRecord inputs
			) {
				// Do nothing
			}
		};
	}

	@Test
	void testContextButtonExecutesThenDeletes() {
		IReplyCallback testEvent = new ButtonTestEvent(contextButton.getId());
		assertTrue(testAction.belongsTo(testEvent));
		testAction.run(testEvent, null);
		assertFalse(testAction.belongsTo(testEvent));
	}
}
