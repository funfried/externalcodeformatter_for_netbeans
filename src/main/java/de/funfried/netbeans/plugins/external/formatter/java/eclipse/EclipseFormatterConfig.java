/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.eclipse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.xml.sax.SAXException;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.eclipse.xml.ConfigReader;

/**
 * Class used for parsing an Eclipse code formatter configuration.
 *
 * @author bahlef
 */
public final class EclipseFormatterConfig {
	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(EclipseFormatterConfig.class.getName());

	/** Prefix for workspace mechanic files. */
	private static final String WORKSPACE_MECHANIC_PREFIX = "/instance/org.eclipse.jdt.core/";

	/** Default configuration of the Eclipse Java formatter. */
	@SuppressWarnings("unchecked")
	private static final Map<String, String> ECLIPSE_JAVA_FORMATTER_DEFAULTS = DefaultCodeFormatterConstants.getJavaConventionsSettings();

	/** {@link Map} holding the source level defaults for the Eclipse Java formatter. */
	private static final Map<String, String> SOURCE_LEVEL_DEFAULTS = new LinkedHashMap<>();

	static {
		String level = JavaCore.VERSION_1_6;
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_COMPLIANCE, level);
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_SOURCE, level);
	}

	/**
	 * Private constructor due to static methods only.
	 */
	private EclipseFormatterConfig() {
	}

	/**
	 * Returns the source level options as a {@link Map}.
	 *
	 * @param sourceLevel the source level for which to get the options
	 *
	 * @return {@link IRegion}
	 */
	private static Map<String, String> getSourceLevelOptions(String sourceLevel) {
		Map<String, String> options = new HashMap<>();
		if (StringUtils.isNotBlank(sourceLevel)) {
			options.put(JavaCore.COMPILER_COMPLIANCE, sourceLevel);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, sourceLevel);
			options.put(JavaCore.COMPILER_SOURCE, sourceLevel);
		}

		return options;
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
			if (EclipseJavaFormatterSettings.isWorkspaceMechanicFile(formatterFile)) {
				configFromFile = readConfigFromWorkspaceMechanicFile(formatterFile);
			} else if (EclipseJavaFormatterSettings.isXMLConfigurationFile(formatterFile)) {
				configFromFile = readConfigFromFormatterXmlFile(formatterFile, formatterProfile);
			} else if (EclipseJavaFormatterSettings.isProjectSetting(formatterFile)) {
				configFromFile = readConfigFromProjectSettings(formatterFile);
			} else {
				configFromFile = new LinkedHashMap<>();
			}

			allConfig.putAll(ECLIPSE_JAVA_FORMATTER_DEFAULTS);
			allConfig.putAll(SOURCE_LEVEL_DEFAULTS);
			allConfig.putAll(configFromFile);
			allConfig.putAll(getSourceLevelOptions(sourceLevel));

			// https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=449262
			if (org.eclipse.jdt.core.JavaCore.DEFAULT_JAVA_FORMATTER.equals(allConfig.get("org.eclipse.jdt.core.javaFormatter"))) {
				//ignore default formatter as configured extension point
				allConfig.remove("org.eclipse.jdt.core.javaFormatter");
			}

			if (null != allConfig.get("org.eclipse.jdt.core.javaFormatter")) {
				throw new UnsupportedOperationException("The use of third-party Java code formatters is not supported by this plugin.\n"
						+ "See https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77\n"
						+ "Try to remove the entry 'org.eclipse.jdt.core.javaFormatter' from the configuration.");
			}
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
