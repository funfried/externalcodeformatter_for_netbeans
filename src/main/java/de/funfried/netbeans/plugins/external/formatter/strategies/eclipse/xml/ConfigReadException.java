package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml;

/**
 * An exception thrown when there is an error reading settings from the code
 * formatter profile of an Eclipse formatter config file.
 * 
 * @author Matt Blanchette
 * @author bahlef
 */
public class ConfigReadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConfigReadException(String message) {
		super(message);
	}
}
