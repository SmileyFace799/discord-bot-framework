package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActionCommandTest {
	private ActionCommand<BotAction.ArgKey> command;

	@BeforeEach
	public void beforeEach() {
		command = new ActionCommand<>(Commands.slash(
				"test",
				"Test command, for unit testing"
		), "nickname");
	}

	@Test
	void testIdentifyingOfCommandWithBaseName() {
		assertTrue(command.identify("test"));
		assertFalse(command.identify("not test"));
	}

	@Test
	void testIdentifyingOfCommandWithNickname() {
		assertTrue(command.identify("nickname"));
		assertFalse(command.identify("not nickname"));
	}
}
