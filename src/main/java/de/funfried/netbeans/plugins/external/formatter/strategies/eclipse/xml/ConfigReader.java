package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.digester3.Digester;

import org.xml.sax.SAXException;

/**
 * This class reads a config file for Eclipse code formatter.
 *
 * @author jecki
 * @author Matt Blanchette
 */
public class ConfigReader {
	/**
	 * Read from the given {@link File} and return it's configuration settings as a
	 * {@link List}.
	 *
	 * @param normalizedFile the {@link File} to read
	 *
	 * @return return {@link List} with all the configurations read from the config file,
	 *         or throws an exception if there's a problem reading the input, e.g.: invalid XML.
	 *
	 * @throws SAXException        if there are parsing issues
	 * @throws IOException         if there is a I/O issue
	 * @throws ConfigReadException if the given file is not a eclipse formatter template
	 */
	public List<Profile> read(File normalizedFile) throws IOException, SAXException, ConfigReadException {
		Digester digester = new Digester();
		digester.addRuleSet(new RuleSet());

		Object result = digester.parse(normalizedFile);
		if (result == null && !(result instanceof Profiles)) {
			throw new ConfigReadException("No profiles found in config file");
		}

		Profiles profiles = (Profiles) result;

		return profiles.getProfiles();
	}
}
