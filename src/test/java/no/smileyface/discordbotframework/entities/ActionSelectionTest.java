package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import no.smileyface.discordbotframework.entities.context.ContextSelection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionSelectionTest {
	@Test
	void testContextSelection() {
		ContextSelection<GenericBotAction.ArgKey> selections =
				new ContextSelection<>(StringSelectMenu::create, null);
		SelectMenu select = selections.getSelectionMenu(builder -> ((StringSelectMenu.Builder) builder)
				.addOption("Test option", "Test Value"));

		assertNotNull(select);
	}
}
