package no.smileyface.discordbotframework.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * A node with a tree-like structure, with a set value type.
 *
 * @param <K> The key type for this node & all its children
 * @param <V> The value type for this node & all its children
 */
public class Node<K, V> {
	private final Map<K, Node<K, V>> children;
	private V value;

	public Node(V value) {
		this.children = new HashMap<>();
		this.value = value;
	}

	public Node() {
		this(null);
	}

	/**
	 * Gets this node's value.
	 *
	 * @return This node's value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Gets this node's value, and casts it to the provided value type.
	 *
	 * @param valueType The class representing the value type to cast to
	 * @param <T> The value type
	 * @return The value of this node, cast to the provided type class
	 * @throws ClassCastException If the value cannot be cast to the provided type
	 */
	public <T extends V> T getValue(Class<T> valueType) throws ClassCastException {
		return valueType.cast(getValue());
	}

	/**
	 * Sets the node's value.
	 *
	 * @param value This node's value
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * Gets an unmodifiable collection of all child nodes.
	 *
	 * @return An unmodifiable collection of all child nodes.
	 */
	public Collection<Node<K, V>> getChildren() {
		return Collections.unmodifiableCollection(children.values());
	}

	/**
	 * Checks if a child node exists.
	 *
	 * @param key The key to check if it has a child node
	 * @return If the specified key has a child node assigned to it or not
	 */
	public boolean hasChild(K key) {
		return children.containsKey(key);
	}

	/**
	 * Gets a specific child node, or an empty node if there is no child node for the provided key.
	 *
	 * @param key The key to find a child node for
	 * @return The found child node, or an empty node if no node is found
	 */
	public @NotNull Node<K, V> getChild(K key) {
		Node<K, V> child = children.get(key);
		return child == null ? new Node<>() : child;
	}

	/**
	 * Adds a child node & sets it to a specified key.
	 * If the key already has a node, the old child is overwritten.
	 *
	 * @param key The key to set the child node to
	 * @param child The child node to add
	 */
	public void addChild(K key, Node<K, V> child) {
		children.put(key, child);
	}

	/**
	 * Gets a child if it's present, otherwise makes a child and returns it.
	 *
	 * @param key The child node's key
	 * @param ifAbsentValue The value to give the newly created node, if one is made
	 * @return The existing or newly created child node
	 */
	public @NotNull Node<K, V> getOrAddChild(K key, V ifAbsentValue) {
		return children.computeIfAbsent(key, k -> new Node<>(ifAbsentValue));
	}

	/**
	 * Shortcut for {@code #getOrAddChild(key, null)}.
	 *
	 * @param key The child node's key
	 * @return The existing or newly created child node
	 */
	public @NotNull Node<K, V> getOrAddChild(K key) {
		return getOrAddChild(key, null);
	}

	public void addChildren(Map<K, Node<K, V>> children) {
		children.forEach(this::addChild);
	}

	@Override
	public String toString() {
		return "Node{"
				+ "children=" + children
				+ ", value='" + value + '\''
				+ '}';
	}
}
