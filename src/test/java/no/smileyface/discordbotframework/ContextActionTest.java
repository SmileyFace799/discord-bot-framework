package no.smileyface.discordbotframework;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.MockEventFactory;
import no.smileyface.discordbotframework.entities.context.ContextAction;
import no.smileyface.discordbotframework.entities.context.ContextButton;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextActionTest {
	private static final ContextAction.Duration DURATION = new ContextAction.Duration(5, TimeUnit.HOURS);

	private ActionManager manager;
	private Identifier identifier;
	private ContextButton<BotAction.ArgKey> contextButton;
	private ContextAction<BotAction.ArgKey> contextAction;

	@BeforeEach
	void setUp() {
		this.manager = new ActionManager(m -> Set.of());
		this.identifier = manager.getIdentifier();
		this.contextButton = new ContextButton<>(ButtonStyle.PRIMARY, "Test Button");

		contextAction = new ContextAction<>(manager, DURATION, true) {
			@Override
			protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
				// Do nothing, testing :)
			}

			@NotNull
			@Override
			protected Collection<? extends ContextButton<ArgKey>> createButtons() {
				return Set.of(contextButton);
			}
		};
	}

	@Test
	void testAddingContextAction() {
		assertTrue(identifier.findButton(contextButton.getId()).isEmpty());
		manager.addContextAction(contextAction);
		assertEquals(contextButton, identifier.findButton(contextButton.getId()).orElseThrow());
		assertEquals(contextAction, identifier.findAction(MockEventFactory.makeButtonEvent(contextButton.getId())).orElseThrow());
	}

	@Test
	void testActionRemovedAfterUse() {
		manager.addContextAction(contextAction);
		manager.onButtonInteraction(MockEventFactory.makeButtonEvent(contextButton.getId()));
		assertTrue(identifier.findButton(contextButton.getId()).isEmpty());
		assertTrue(identifier.findAction(MockEventFactory.makeButtonEvent(contextButton.getId())).isEmpty());

	}
}
