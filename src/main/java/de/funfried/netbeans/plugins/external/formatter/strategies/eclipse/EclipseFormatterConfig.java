/*
 * Copyright (c) 2019 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.xml.sax.SAXException;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml.ConfigReader;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class EclipseFormatterConfig {
	private static final Logger log = Logger.getLogger(EclipseFormatterConfig.class.getName());

	private static final String WORKSPACE_MECHANIC_PREFIX = "/instance/org.eclipse.jdt.core/";

	@SuppressWarnings("unchecked")
	private static final Map<String, String> ECLIPSE_JAVA_FORMATTER_DEFAULTS = DefaultCodeFormatterConstants.getJavaConventionsSettings();

	private static final Map<String, String> SOURCE_LEVEL_DEFAULTS = new LinkedHashMap<>();

	static {
		String level = JavaCore.VERSION_1_6;
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_COMPLIANCE, level);
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_SOURCE, level);
	}

	private EclipseFormatterConfig() {
	}

	private static Map<String, String> getSourceLevelOptions(String sourceLevel) {
		Map<String, String> options = new HashMap<>();
		if (null != sourceLevel && !"".equals(sourceLevel)) {
			String level = sourceLevel;
			options.put(JavaCore.COMPILER_COMPLIANCE, level);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
			options.put(JavaCore.COMPILER_SOURCE, level);
		}

		return options;
	}

	public static Map<String, String> parseConfig(String formatterFile, String formatterProfile, String sourceLevel)
			throws ProfileNotFoundException, ConfigReadException, CannotLoadConfigurationException {
		Map<String, String> allConfig = new HashMap<>();
		try {
			Map<String, String> configFromFile;
			if (Settings.isWorkspaceMechanicFile(formatterFile)) {
				configFromFile = readConfigFromWorkspaceMechanicFile(formatterFile);
			} else if (Settings.isXMLConfigurationFile(formatterFile)) {
				configFromFile = readConfigFromFormatterXmlFile(formatterFile, formatterProfile);
			} else if (Settings.isProjectSetting(formatterFile)) {
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

	private static Map<String, String> readConfigFromFormatterXmlFile(String formatterFile, String formatterProfile) throws ConfigReadException, ProfileNotFoundException, IOException, SAXException {
		return ConfigReader.getProfileSettings(ConfigReader.toFileObject(formatterFile), formatterProfile);
	}

	private static Map<String, String> readConfigFromWorkspaceMechanicFile(String formatterFile) throws IOException {
		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(formatterFile)) {
			properties.load(is);
		}

		return properties.keySet().stream().filter(key -> ((String) key).startsWith(WORKSPACE_MECHANIC_PREFIX))
				.collect(Collectors.toMap(key -> ((String) key).substring(WORKSPACE_MECHANIC_PREFIX.length()), key -> properties.getProperty((String) key)));
	}

	private static Map<String, String> readConfigFromProjectSettings(final String formatterFile) throws IOException {
		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(formatterFile)) {
			properties.load(is);
		}

		return properties.keySet().stream().collect(Collectors.toMap(key -> (String) key, key -> properties.getProperty((String) key)));
	}
}
