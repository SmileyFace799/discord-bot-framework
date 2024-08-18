package no.smileyface.discordbotframework.entities;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import no.smileyface.discordbotframework.data.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActionInputTest {
	private ActionCommand<TestKey> command;
	private ActionButton<TestKey> button;
	private ActionModal<TestKey> modal;
	private ActionSelection<TestKey> selection;
	private BotAction<TestKey> action;

	@BeforeEach
	public void beforeEach() {
		this.command = new ActionCommand<>(Commands.slash(
				"commandtest",
				"Test command, for unit testing"
		), "nickname");
		this.button = new ActionButton<>(ButtonStyle.PRIMARY, "buttontest", "Test button");
		this.modal = new ActionModal<>(
				"modaltest",
				"Test modal",
				List.of(TextInput.create(
						"text input test",
						"Test text input",
						TextInputStyle.SHORT
				).build())
		);
		this.selection = new ActionSelection<>(StringSelectMenu
				.create("selectiontest")
				.addOption("test option", "test value"),
				TestKey.SELECTION_VALUES
		);
		this.action = new BotAction<>(null) {
			@NotNull
			@Override
			protected Collection<ActionCommand<TestKey>> createCommands() {
				return Set.of(command);
			}

			@NotNull
			@Override
			protected Collection<ActionButton<TestKey>> createButtons() {
				return Set.of(button);
			}

			@NotNull
			@Override
			protected Collection<ActionModal<TestKey>> createModals() {
				return Set.of(modal);
			}

			@NotNull
			@Override
			protected Collection<ActionSelection<TestKey>> createSelections() {
				return Set.of(selection);
			}

			@Override
			protected void execute(
					IReplyCallback event,
					Node<TestKey, Object> args
			) {
				// Do nothing, just testing :)
			}
		};
	}

	@Test
	void testIdentifyingOfCommandWithBaseName() {
		assertTrue(command.identify("commandtest"));
		assertFalse(command.identify("not commandtest"));
	}

	@Test
	void testIdentifyingOfCommandWithNickname() {
		assertTrue(command.identify("nickname"));
		assertFalse(command.identify("not nickname"));
	}

	@Test
	void testIdentifyingOfButtonById() {
		assertTrue(button.identify("buttontest"));
		assertFalse(button.identify("not buttontest"));
	}

	@Test
	void testIdentifyingOfModalById() {
		assertTrue(modal.identify("modaltest"));
		assertFalse(modal.identify("not modaltest"));
	}

	@Test
	void testIdentifyingOfSelectionById() {
		assertTrue(selection.identify("selectiontest"));
		assertFalse(selection.identify("not selectiontest"));
	}

	@Test
	void testActionBelongsTo() {
		assertTrue(action.belongsTo(MockEventFactory.makeCommandEvent("commandtest")));
		assertFalse(action.belongsTo(MockEventFactory.makeCommandEvent("not commandtest")));

		assertTrue(action.belongsTo(MockEventFactory.makeCommandEvent("nickname")));
		assertFalse(action.belongsTo(MockEventFactory.makeCommandEvent("not nickname")));

		assertTrue(action.belongsTo(MockEventFactory.makeButtonEvent("buttontest")));
		assertFalse(action.belongsTo(MockEventFactory.makeButtonEvent("not buttontest")));

		assertTrue(action.belongsTo(MockEventFactory.makeModalEvent("modaltest")));
		assertFalse(action.belongsTo(MockEventFactory.makeModalEvent("not modaltest")));

		assertTrue(action.belongsTo(MockEventFactory.makeSelectionEvent("selectiontest")));
		assertFalse(action.belongsTo(MockEventFactory.makeSelectionEvent("not selectiontest")));
	}

	@Test
	void testActionBelongsToNoCrossIdentifying() {
		assertFalse(action.belongsTo(MockEventFactory.makeButtonEvent("commandtest")));
		assertFalse(action.belongsTo(MockEventFactory.makeModalEvent("commandtest")));
		assertFalse(action.belongsTo(MockEventFactory.makeSelectionEvent("commandtest")));
		
		assertFalse(action.belongsTo(MockEventFactory.makeCommandEvent("buttontest")));
		assertFalse(action.belongsTo(MockEventFactory.makeModalEvent("buttontest")));
		assertFalse(action.belongsTo(MockEventFactory.makeSelectionEvent("buttontest")));

		assertFalse(action.belongsTo(MockEventFactory.makeCommandEvent("modaltest")));
		assertFalse(action.belongsTo(MockEventFactory.makeButtonEvent("modaltest")));
		assertFalse(action.belongsTo(MockEventFactory.makeSelectionEvent("modaltest")));

		assertFalse(action.belongsTo(MockEventFactory.makeCommandEvent("selectiontest")));
		assertFalse(action.belongsTo(MockEventFactory.makeButtonEvent("selectiontest")));
		assertFalse(action.belongsTo(MockEventFactory.makeModalEvent("selectiontest")));
	}

	private enum TestKey implements BotAction.ArgKey {
		SELECTION_VALUES
	}
}
