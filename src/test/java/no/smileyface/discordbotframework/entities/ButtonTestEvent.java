package no.smileyface.discordbotframework.entities;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

class ButtonTestEvent extends ButtonInteractionEvent {
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
