package no.smileyface.discordbotframework.files;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An interface for persistent storage of any object, serialized as bytes.
 *
 * @param <T> The object type to store
 */
public abstract class FileInterface<T> {
	private static final Path BASE_PATH = FileSystems.getDefault().getPath("botFiles");

	private final Path path;
	private T value;

	/**
	 * Creates the file interface.
	 *
	 * @param path The path to the file this interface is for
	 * @throws IOException If the file at the provided path cannot be read
	 */
	protected FileInterface(String path) throws IOException {
		this(path, true);
	}

	/**
	 * Creates the file interface.
	 *
	 * @param path The path to the file this interface is for
	 * @param load If the stored value should be immediately loaded from the serialized file
	 * @throws IOException If the file at the provided path cannot be read
	 */
	protected FileInterface(String path, boolean load) throws IOException {
		this.path = BASE_PATH.resolve(path);
		try {
			Files.createFile(this.path);
		} catch (FileAlreadyExistsException ignored) {
			// File exists, all good
		}
		if (load) {
			load();
		}
	}

	/**
	 * Set the stored value.
	 * Can be overridden and made public.
	 *
	 * @param value The stored value.
	 * @throws IOException If The new value can't be saved
	 */
	protected void set(T value) throws IOException {
		this.value = value;
		save();
	}

	/**
	 * Modifies the stored value, and saves the value again with its updated state.
	 *
	 * @param valueConsumer A consumer that may update the state of the value
	 * @throws IOException If an I/O exception occurred while saving the value
	 * @see #modifyAndGet(Function)
	 */
	protected final void modify(Consumer<T> valueConsumer) throws IOException {
		valueConsumer.accept(value);
		save();
	}

	/**
	 * <p>Applies a function to the stored value, and returns the function's return value.</p>
	 * <p><b>NOTE:</b> The function should never change the state of the value,
	 * it should only be used to return something dependent on the value's state.
	 * If the value's state is updated, these updates will not be saved.
	 * For modifying the state of the object,
	 * use {@link #modify(Consumer)} or {@link #modifyAndGet(Function)}</p>
	 *
	 * @param valueFunction The function to apply to the value.
	 *                      This should not modify the value's state
	 * @param <R>           The return type of the value function
	 * @return The return value of the value function
	 */
	protected final <R> R get(Function<T, R> valueFunction) {
		return valueFunction.apply(value);
	}

	/**
	 * Modifies the stored value, saves the value again with its updated state,
	 * and returns something dependent on the value's state.
	 * If the value may never be modified as a result of the function,
	 * use {@link #get(Function)} instead.
	 *
	 * @param valueFunction A function that may update the state of the value
	 * @param <R>           The return tpe of the value function
	 * @return The return value of the value function
	 * @throws IOException If an I/O exception occurred while saving the value
	 * @see #modify(Consumer)
	 */
	protected final <R> R modifyAndGet(Function<T, R> valueFunction) throws IOException {
		R returnValue = valueFunction.apply(value);
		save();
		return returnValue;
	}

	protected final void load() throws IOException {
		byte[] bytes = Files.readAllBytes(this.path);
		if (bytes.length > 0) {
			set(fromBytes(bytes));
		} else {
			set(fromNothing());
		}
	}

	protected final void save() throws IOException {
		Files.write(path, value == null ? new byte[]{} : toBytes(value));
	}

	protected T fromNothing() {
		return null;
	}

	/**
	 * Converts the serialized value into a real values.
	 *
	 * @param bytes The bytes of the serialized value
	 * @return The recreated value
	 */
	protected abstract T fromBytes(byte[] bytes);

	/**
	 * Serializes the stored value into bytes.
	 *
	 * @param value The stored value to serialize
	 * @return The serialized bytes
	 */
	protected abstract byte[] toBytes(T value);
}
