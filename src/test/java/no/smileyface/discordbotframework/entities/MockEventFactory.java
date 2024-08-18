package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

public class MockEventFactory {
	private MockEventFactory() {
		throw new IllegalStateException("Static factory class");
	}

	public static SlashCommandInteractionEvent makeCommandEvent(String name) {
		return new CommandTestEvent(name);
	}

	public static ButtonInteractionEvent makeButtonEvent(String id) {
		return new ButtonTestEvent(id);
	}

	public static ModalInteractionEvent makeModalEvent(String id) {
		return new ModalTestEvent(id);
	}

	public static <T, S extends SelectMenu> GenericSelectMenuInteractionEvent<T, S> makeSelectionEvent(
		String id
	) {
		return new SelectionTestEvent<>(id);
	}

	private static class CommandTestEvent extends SlashCommandInteractionEvent {
		private final String name;

		public CommandTestEvent(String name) {
			super(null, 0, null);
			this.name = name;
		}

		@NotNull
		@Override
		public String getName() {
			return this.name;
		}
	}

	private static class ButtonTestEvent extends ButtonInteractionEvent {
		private final String id;

		public ButtonTestEvent(String id) {
			super(null, 0, null);
			this.id = id;
		}

		@NotNull
		@Override
		public String getComponentId() {
			return this.id;
		}
	}

	private static class ModalTestEvent extends ModalInteractionEvent {
		private final String id;

		public ModalTestEvent(String id) {
			super(null, 0, null);
			this.id = id;
		}

		@NotNull
		@Override
		public String getModalId() {
			return this.id;
		}
	}

	private static class SelectionTestEvent<T, S extends SelectMenu>
			extends GenericSelectMenuInteractionEvent<T, S> {
		private final String id;

		public SelectionTestEvent(String id) {
			super(null, 0, null);
			this.id = id;
		}

		@NotNull
		@Override
		public String getComponentId() {
			return this.id;
		}
	}
}
