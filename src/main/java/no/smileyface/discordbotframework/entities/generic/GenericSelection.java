package no.smileyface.discordbotframework.entities.generic;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.Identifiable;
import org.jetbrains.annotations.NotNull;

/**
 * A selection menu for executing actions by selecting something.
 *
 * @param <K> Key type used for args returned from
 * 			  {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}
 */
public abstract class GenericSelection<K extends GenericBotAction.ArgKey> implements Identifiable {
	private final Supplier<SelectMenu.Builder<?, ?>> builderSupplier;
	private final String id;
	private final K nextValueKey;

	/**
	 * Creates an action selection.
	 *
	 * @param builderSupplier A supplier for the "base" selection menu to use
	 * @param nextValueKey    The key to use for the next selected value in the value node provided
	 *                        by {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}
	 */
	protected GenericSelection(Supplier<SelectMenu.Builder<?, ?>> builderSupplier, K nextValueKey) {
		this.builderSupplier = builderSupplier;
		this.id = builderSupplier.get().getId();
		this.nextValueKey = nextValueKey;
	}

	public final K getNextValueKey() {
		return nextValueKey;
	}

	/**
	 * Gets a modified version of this selection's {@link SelectMenu}. This does not modify the
	 * stored selection menu, and instead creates a new instance.
	 *
	 * @param buildConsumer A consumer with instructions on how to modify the selection menu.
	 *                      If the builder accepted by this consumer were to be built
	 *                      without being modified, the resulting selection menu would be
	 *                      an identical copy of the stored selection menu
	 * @return A modified copy of the stored selection menu
	 * @see #getSelectMenu()
	 */
	public final SelectMenu getSelectionMenu(
			@NotNull Consumer<SelectMenu.Builder<?, ?>> buildConsumer
	) {
		SelectMenu.Builder<?, ?> builder = builderSupplier.get();
		buildConsumer.accept(builder);
		return builder.build();
	}

	/**
	 * Gets this selection's {@link SelectMenu}.
	 *
	 * @return The stored selection menu
	 * @see #getSelectionMenu(Consumer)
	 */
	public final SelectMenu getSelectMenu() {
		return builderSupplier.get().build();
	}

	/**
	 * <p>Creates a node of arguments to use when
	 * executing the action associated with this selection.
	 * This creates a linked list of any selected values,
	 * where the next value can be acquired with the {@code nextValueKey} provided in the
	 * {@link #GenericSelection(Supplier, GenericBotAction.ArgKey) constructor}.</p>
	 * <p>{@link #getNodeRootValue()} is used to set the value of the root node, and
	 * {@link #addSelectionArgs(GenericSelectMenuInteractionEvent, Node)}
	 * is used to add more arguments to the arg node (These methods can be overridden).
	 * These methods are both called before the selected values are added,
	 * ensuring that the selected values are always included.</p>
	 *
	 * @param event The event representing the selection
	 * @return A node of action arguments, in the form of a linked list of values by default
	 */
	public final Node<K, Object> getSelectionArgs(GenericSelectMenuInteractionEvent<?, ?> event) {
		Node<K, Object> args = new Node<>(getNodeRootValue());
		addSelectionArgs(event, args);

		K key = getNextValueKey();
		Node<K, Object> parent = args;
		for (Object value : event.getValues()) {
			parent.addChild(key, new Node<>(value));
			parent = parent.getChild(key);
		}
		return args;
	}

	/**
	 * Override this to set the value of the arg node in
	 * {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}.
	 * Returns {@code null} by default.
	 *
	 * @return The value for the root arg node ({@code null} by default)
	 */
	protected Object getNodeRootValue() {
		return null;
	}

	/**
	 * Override this to add values to the arg node in
	 * {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}.
	 * This is called before the selected values are added. Does nothing by default
	 *
	 * @param event The event representing the selection
	 * @param args The arg node to add argument to (this is always the root node)
	 */
	protected void addSelectionArgs(
			GenericSelectMenuInteractionEvent<?, ?> event,
			Node<K, Object> args
	) {
		// Add nothing by default
	}

	@Override
	public boolean identify(String id) {
		return this.id.equalsIgnoreCase(id);
	}
}
