package no.smileyface.discordbotframework;

import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;
import no.smileyface.discordbotframework.entities.noncontext.ActionModal;
import no.smileyface.discordbotframework.entities.noncontext.ActionSelection;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.MockEventFactory;
import no.smileyface.discordbotframework.entities.BotAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdentifierTest {
	private static final String COMMAND_NAME = "commandTest".toLowerCase();
	private static final String NOT_COMMAND = "notCommandTest".toLowerCase();
	private static final String COMMAND_NICKNAME = "nickname".toLowerCase();
	private static final String NOT_NICKNAME = "notNickname".toLowerCase();

	private static final String BUTTON_NAME = "buttonTest".toLowerCase();
	private static final String NOT_BUTTON = "notButtonTest".toLowerCase();

	private static final String MODAL_NAME = "modalTest".toLowerCase();
	private static final String NOT_MODAL = "notModalTest".toLowerCase();

	private static final String SELECTION_NAME = "selectionTest".toLowerCase();
	private static final String NOT_SELECTION = "notSelectionTest".toLowerCase();

	private ActionCommand<TestKey> command;
	private ActionButton<TestKey> button;
	private ActionModal<TestKey> modal;
	private ActionSelection<TestKey> selection;
	private BotAction<TestKey> action;
	private Identifier identifier;

	static class TestCommand extends ActionCommand<TestKey> {
		TestCommand() {
			super(Commands.slash(
					COMMAND_NAME,
					"Test command, for unit testing"
			), COMMAND_NICKNAME);
		}
	}

	static class TestButton extends ActionButton<TestKey> {
		TestButton() {
			super(ButtonStyle.PRIMARY, BUTTON_NAME, "Test button");
		}
	}

	static class TestModal extends ActionModal<TestKey> {
		TestModal() {
			super(
					MODAL_NAME,
					"Test modal",
					List.of(TextInput.create(
							"text input test",
							"Test text input",
							TextInputStyle.SHORT
					).build())
			);
		}
	}

	static class TestSelection extends ActionSelection<TestKey> {
		TestSelection() {
			super(
					() -> StringSelectMenu
							.create(SELECTION_NAME)
							.addOption("test option", "test value"),
					TestKey.SELECTION_VALUES
			);
		}
	}

	@BeforeEach
	public void beforeEach() {
		this.command = new TestCommand();
		this.button = new TestButton();
		this.modal = new TestModal();
		this.selection = new TestSelection();
		this.action = new BotAction<>(null, command) {
			@Override
			protected void execute(
					IReplyCallback event,
					Node<TestKey, Object> args
			) {
				// Do nothing, just testing :)
			}
		};
		action.addButtons(button);
		action.addModals(modal);
		action.addSelections(selection);
		this.identifier = new Identifier(Set.of(action));
	}

	@Test
	void testIdentifyingOfCommandWithBaseName() {
		assertTrue(command.identify(COMMAND_NAME));
		assertFalse(command.identify(NOT_COMMAND));

		assertEquals(command, assertDoesNotThrow(() ->
				identifier.findCommand(COMMAND_NAME).orElseThrow()
		));
		assertTrue(identifier.findCommand(NOT_COMMAND).isEmpty());
	}

	@Test
	void testIdentifyingOfCommandWithNickname() {
		assertTrue(command.identify(COMMAND_NICKNAME));
		assertFalse(command.identify(NOT_NICKNAME));

		assertEquals(command, assertDoesNotThrow(() ->
				identifier.findCommand(COMMAND_NICKNAME).orElseThrow()
		));
		assertTrue(identifier.findCommand(NOT_NICKNAME).isEmpty());
	}

	@Test
	void testIdentifyingOfCommandWithClass() {
		assertEquals(command, assertDoesNotThrow(() ->
				identifier.findCommand(TestCommand.class).orElseThrow()
		));
	}

	@Test
	void testIdentifyingOfButtonById() {
		assertTrue(button.identify(BUTTON_NAME));
		assertFalse(button.identify(NOT_BUTTON));

		assertEquals(button, assertDoesNotThrow(() ->
				identifier.findButton(BUTTON_NAME).orElseThrow()
		));
		assertTrue(identifier.findButton(NOT_BUTTON).isEmpty());
	}

	@Test
	void testIdentifyingOfButtonByClass() {
		assertEquals(button, assertDoesNotThrow(() ->
				identifier.findButton(TestButton.class).orElseThrow()
		));
	}

	@Test
	void testIdentifyingOfModalById() {
		assertTrue(modal.identify(MODAL_NAME));
		assertFalse(modal.identify(NOT_MODAL));

		assertEquals(modal, assertDoesNotThrow(() ->
				identifier.findModal(MODAL_NAME).orElseThrow()
		));
		assertTrue(identifier.findModal(NOT_MODAL).isEmpty());
	}

	@Test
	void testIdentifyingOfModalByClass() {
		assertEquals(modal, assertDoesNotThrow(() ->
				identifier.findModal(TestModal.class).orElseThrow()
		));
	}

	@Test
	void testIdentifyingOfSelectionById() {
		assertTrue(selection.identify(SELECTION_NAME));
		assertFalse(selection.identify(NOT_SELECTION));

		assertEquals(selection, assertDoesNotThrow(() ->
				identifier.findSelection(SELECTION_NAME).orElseThrow()
		));
		assertTrue(identifier.findSelection(NOT_SELECTION).isEmpty());
	}

	private void withEvent(IReplyCallback event, boolean expected) {
		if (expected) {
			assertEquals(action, assertDoesNotThrow(() ->
					identifier.findAction(event).orElseThrow()
			));
		} else {
			assertTrue(identifier.findAction(event).isEmpty());
		}

	}

	@Test
	void testIdentifyingOfAction() {
		withEvent(MockEventFactory.makeCommandEvent(COMMAND_NAME), true);
		withEvent(MockEventFactory.makeCommandEvent(NOT_COMMAND), false);

		withEvent(MockEventFactory.makeCommandEvent(COMMAND_NICKNAME), true);
		withEvent(MockEventFactory.makeCommandEvent(NOT_NICKNAME), false);

		withEvent(MockEventFactory.makeButtonEvent(BUTTON_NAME), true);
		withEvent(MockEventFactory.makeButtonEvent(NOT_BUTTON), false);

		withEvent(MockEventFactory.makeModalEvent(MODAL_NAME), true);
		withEvent(MockEventFactory.makeModalEvent(NOT_MODAL), false);

		withEvent(MockEventFactory.makeSelectionEvent(SELECTION_NAME), true);
		withEvent(MockEventFactory.makeSelectionEvent(NOT_SELECTION), false);
	}

	@Test
	void testActionBelongsToNoCrossIdentifying() {
		withEvent(MockEventFactory.makeButtonEvent(COMMAND_NAME), false);
		withEvent(MockEventFactory.makeModalEvent(COMMAND_NAME), false);
		withEvent(MockEventFactory.makeSelectionEvent(COMMAND_NAME), false);

		withEvent(MockEventFactory.makeCommandEvent(BUTTON_NAME), false);
		withEvent(MockEventFactory.makeModalEvent(BUTTON_NAME), false);
		withEvent(MockEventFactory.makeSelectionEvent(BUTTON_NAME), false);

		withEvent(MockEventFactory.makeCommandEvent(MODAL_NAME), false);
		withEvent(MockEventFactory.makeButtonEvent(MODAL_NAME), false);
		withEvent(MockEventFactory.makeSelectionEvent(MODAL_NAME), false);

		withEvent(MockEventFactory.makeCommandEvent(SELECTION_NAME), false);
		withEvent(MockEventFactory.makeButtonEvent(SELECTION_NAME), false);
		withEvent(MockEventFactory.makeModalEvent(SELECTION_NAME), false);
	}

	private enum TestKey implements GenericBotAction.ArgKey {
		SELECTION_VALUES
	}
}
