/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.javascript.eclipse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.wst.jsdt.core.formatter.DefaultCodeFormatterConstants;
import org.xml.sax.SAXException;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.java.eclipse.xml.ConfigReader;

/**
 * Class used for parsing an Eclipse code formatter configuration.
 *
 * @author bahlef
 */
public final class EclipseFormatterConfig {
	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(EclipseFormatterConfig.class.getName());

	/** Prefix for workspace mechanic files. */
	private static final String WORKSPACE_MECHANIC_PREFIX = "/instance/org.eclipse.wst.jsdt.core/";

	/** Default configuration of the Eclipse Java formatter. */
	@SuppressWarnings("unchecked")
	private static final Map<String, String> ECLIPSE_JAVA_FORMATTER_DEFAULTS = DefaultCodeFormatterConstants.getJSLintConventionsSettings();

	/**
	 * Private constructor due to static methods only.
	 */
	private EclipseFormatterConfig() {
	}

	/**
	 * Parses the configuration parameters from the given {@code profile} of the
	 * given formatter configuration file and returns it as a {@link Map}
	 * containing the configuration as key value pairs.
	 *
	 * @param formatterFile    the path to the formatter configuration file
	 * @param formatterProfile the name of the formatter configuration profile
	 * @param sourceLevel      the source level to use for formatting
	 *
	 * @return a {@link Map} containing the configuration as key value pairs
	 *
	 * @throws ConfigReadException              if there is an issue parsing the formatter configuration
	 * @throws ProfileNotFoundException         if the given {@code profile} could not be found
	 * @throws CannotLoadConfigurationException if there is any issue accessing or reading the formatter configuration
	 */
	public static Map<String, String> parseConfig(String formatterFile, String formatterProfile, String sourceLevel)
			throws ProfileNotFoundException, ConfigReadException, CannotLoadConfigurationException {
		Map<String, String> allConfig = new HashMap<>();
		try {
			Map<String, String> configFromFile;
			if (EclipseJavascriptFormatterSettings.isWorkspaceMechanicFile(formatterFile)) {
				configFromFile = readConfigFromWorkspaceMechanicFile(formatterFile);
			} else if (EclipseJavascriptFormatterSettings.isXMLConfigurationFile(formatterFile)) {
				configFromFile = readConfigFromFormatterXmlFile(formatterFile, formatterProfile);
			} else if (EclipseJavascriptFormatterSettings.isProjectSetting(formatterFile)) {
				configFromFile = readConfigFromProjectSettings(formatterFile);
			} else {
				configFromFile = new LinkedHashMap<>();
			}

			allConfig.putAll(ECLIPSE_JAVA_FORMATTER_DEFAULTS);
			allConfig.putAll(configFromFile);
		} catch (ConfigReadException | ProfileNotFoundException ex) {
			log.log(Level.WARNING, "Could not load configuration: " + formatterFile, ex);

			throw ex;
		} catch (Exception ex) {
			log.log(Level.WARNING, "Could not load configuration: " + formatterFile, ex);

			throw new CannotLoadConfigurationException(ex);
		}

		return allConfig;
	}

	/**
	 * Parses and returns the properties of the given {@code formatterProfile} inside the
	 * given {@code formatterFile} into a key value {@link Map}.
	 *
	 * @param formatterFile    the configuration file
	 * @param formatterProfile the profile which should be read from the configuration file
	 * @return the properties of the given {@code formatterProfile} inside the
	 *         given {@code formatterFile} as a key value {@link Map}
	 *
	 * @throws ConfigReadException      if there is an issue parsing the formatter configuration
	 * @throws ProfileNotFoundException if the given {@code profile} could not be found
	 * @throws IOException              if there is an issue accessing the given configuration file
	 * @throws SAXException             if there are parsing issues
	 */
	private static Map<String, String> readConfigFromFormatterXmlFile(String formatterFile, String formatterProfile) throws ConfigReadException, ProfileNotFoundException, IOException, SAXException {
		return ConfigReader.getProfileSettings(ConfigReader.toFileObject(formatterFile), formatterProfile);
	}

	/**
	 * Parses and returns properties of the given workspace mechanic {@code formatterFile}
	 * into a key value {@link Map}.
	 *
	 * @param formatterFile the workspace mechanic configuration file
	 *
	 * @return properties of the given workspace mechanic {@code formatterFile}
	 *         as a key value {@link Map}
	 *
	 * @throws IOException if there is an issue accessing the given workspace mechanic configuration file
	 */
	private static Map<String, String> readConfigFromWorkspaceMechanicFile(String formatterFile) throws IOException {
		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(formatterFile)) {
			properties.load(is);
		}

		return properties.keySet().stream().filter(key -> ((String) key).startsWith(WORKSPACE_MECHANIC_PREFIX))
				.collect(Collectors.toMap(key -> ((String) key).substring(WORKSPACE_MECHANIC_PREFIX.length()), key -> properties.getProperty((String) key)));
	}

	/**
	 * Parses and returns properties of the given project settings into a key
	 * value {@link Map}.
	 *
	 * @param formatterFile the project settings file
	 *
	 * @return properties of the given project settings as a key value
	 *         {@link Map}
	 *
	 * @throws IOException if there is an issue accessing the given project settings file
	 */
	private static Map<String, String> readConfigFromProjectSettings(final String formatterFile) throws IOException {
		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(formatterFile)) {
			properties.load(is);
		}

		return properties.keySet().stream().collect(Collectors.toMap(key -> (String) key, key -> properties.getProperty((String) key)));
	}
}
