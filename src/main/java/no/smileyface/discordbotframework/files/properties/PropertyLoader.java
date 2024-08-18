package no.smileyface.discordbotframework.files.properties;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;
import no.smileyface.discordbotframework.data.Node;

/**
 * Utility class for loading bot properties.
 */
public class PropertyLoader {
	private PropertyLoader() {
		throw new IllegalStateException("Utility class");
	}

	private static void throwLine(LineNumberReader reader) throws PropertyLoadException {
		throw new PropertyLoadException(String.format(
				"Invalid .properties formatting (Line %s)",
				reader.getLineNumber()
		));
	}

	/**
	 * Loads the property tree from the .properties file.
	 *
	 * @throws PropertyLoadException If loading the properties failed
	 */
	public static Node<String, String> loadProperties() throws PropertyLoadException {
		Node<String, String> root = new Node<>();

		Path filename;
		try (Stream<Path> fileStream = Files.list(FileSystems.getDefault().getPath(""))) {
			PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.properties");
			filename = fileStream
					.filter(file -> matcher.matches(file.getFileName()))
					.findFirst()
					.orElseThrow(() -> new PropertyLoadException("No .properties file found"));
		} catch (IOException ioe) {
			throw new PropertyLoadException(
					"Could not read files in bot directory, unable to find .properties file",
					ioe
			);
		}
		try (LineNumberReader reader = new LineNumberReader(new FileReader(filename.toFile()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isBlank()) {
					String[] splitLine = line.split("=", 2);
					if (splitLine.length < 2) {
						throwLine(reader);
					}
					String[] treePath = splitLine[0].strip().split("\\.");
					Node<String, String> currentNode = root;
					for (String node : treePath) {
						currentNode = currentNode.getOrAddChild(node);
					}
					currentNode.setValue(splitLine[1].strip());
				}
			}
		} catch (IOException ioe) {
			throw new PropertyLoadException("Could not load the .properties file", ioe);
		}
		return root;
	}


}
