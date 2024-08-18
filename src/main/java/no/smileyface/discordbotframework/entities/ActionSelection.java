package no.smileyface.discordbotframework.entities;

import java.util.function.Function;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import no.smileyface.discordbotframework.data.Node;

/**
 * A selection menu for executing actions by selecting something.
 *
 * @param <K> Key type used for args returned from
 * 			  {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}
 */
public class ActionSelection<K extends BotAction.ArgKey>
		implements Identifiable {
	private final SelectMenu.Builder<?, ?> selectionMenuBuilder;
	private final K nextValueKey;

	/**
	 * Creates an action selection.
	 *
	 * @param selectionMenuBuilder The selection menu to use when displaying the selection
	 * @param nextValueKey The key to use for the next selected value in the value node provided by
	 * 				   {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}
	 */
	public ActionSelection(SelectMenu.Builder<?, ?> selectionMenuBuilder, K nextValueKey) {
		this.selectionMenuBuilder = selectionMenuBuilder;
		this.nextValueKey = nextValueKey;
	}

	protected final K getNextValueKey() {
		return nextValueKey;
	}

	/**
	 * Gets the selection menu, that can be modified before being built.
	 *
	 * @param buildFunction A function with instructions on how to build the selection menu.
	 *                      If this is {@code null}, the selection menu will be returned as-is
	 * @return The stored selection menu, possibly modified if a build function is provided
	 * @see #getSelectMenu()
	 */
	public final SelectMenu getSelectionMenu(
			Function<SelectMenu.Builder<?, ?>, SelectMenu> buildFunction
	) {
		return buildFunction == null
				? selectionMenuBuilder.build()
				: buildFunction.apply(selectionMenuBuilder);
	}

	/**
	 * Shortcut for {@code #getSelectionMenu(null)}.
	 *
	 * @return The stored selection menu, unmodified
	 * @see #getSelectionMenu(Function)
	 */
	public final SelectMenu getSelectMenu() {
		return getSelectionMenu(null);
	}

	/**
	 * <p>Creates a node of arguments to use when
	 * executing the action associated with this selection.
	 * This creates a linked list of any selected values,
	 * where the next value can be acquired with the {@code nextValueKey} provided in the
	 * {@link #ActionSelection(SelectMenu.Builder, BotAction.ArgKey) constructor}.</p>
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
		return selectionMenuBuilder.getId().equalsIgnoreCase(id);
	}
}
