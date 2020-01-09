package de.funfried.netbeans.plugins.external.formatter.exceptions;

/**
 * An exception thrown when there is an error reading settings from the code
 * formatter profile of an Eclipse formatter config file.
 * 
 * @author Matt Blanchette
 * @author bahlef
 */
public class ConfigReadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of the {@link ConfigReadException}.
	 *
	 * @param message the message
	 */
	public ConfigReadException(String message) {
		super(message);
	}
}
