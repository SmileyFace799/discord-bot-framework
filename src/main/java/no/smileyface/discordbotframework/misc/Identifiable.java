package no.smileyface.discordbotframework.misc;


/**
 * Anything that can be identified with one or more ID string(s).
 */
public interface Identifiable {

	/**
	 * Checks if this identifies with the given ID.
	 *
	 * @param id The ID to check if this identifies with
	 * @return If this identifies with the given ID
	 */
	boolean identify(String id);
}
