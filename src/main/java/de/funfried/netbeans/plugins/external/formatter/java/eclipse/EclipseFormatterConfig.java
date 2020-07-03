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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import de.funfried.netbeans.plugins.external.formatter.eclipse.xml.EclipseFormatterUtils;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;

/**
 * Class used for parsing an Eclipse code formatter configuration.
 *
 * @author bahlef
 */
public final class EclipseFormatterConfig {
	/** Prefix for workspace mechanic files. */
	private static final String WORKSPACE_MECHANIC_PREFIX = "/instance/org.eclipse.jdt.core/";

	/** Default configuration of the Eclipse Java formatter. */
	@SuppressWarnings("unchecked")
	private static final Map<String, String> ECLIPSE_JAVA_FORMATTER_DEFAULTS = new LinkedHashMap<>();

	static {
		ECLIPSE_JAVA_FORMATTER_DEFAULTS.putAll(DefaultCodeFormatterConstants.getJavaConventionsSettings());

		String level = JavaCore.VERSION_1_6;
		ECLIPSE_JAVA_FORMATTER_DEFAULTS.put(JavaCore.COMPILER_COMPLIANCE, level);
		ECLIPSE_JAVA_FORMATTER_DEFAULTS.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
		ECLIPSE_JAVA_FORMATTER_DEFAULTS.put(JavaCore.COMPILER_SOURCE, level);
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
		Map<String, String> config = EclipseFormatterUtils.parseConfig(formatterFile, formatterProfile, ECLIPSE_JAVA_FORMATTER_DEFAULTS, getSourceLevelOptions(sourceLevel), WORKSPACE_MECHANIC_PREFIX,
				EclipseJavaFormatterSettings.PROJECT_PREF_FILE);

		// https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=449262
		if (org.eclipse.jdt.core.JavaCore.DEFAULT_JAVA_FORMATTER.equals(config.get("org.eclipse.jdt.core.javaFormatter"))) {
			//ignore default formatter as configured extension point
			config.remove("org.eclipse.jdt.core.javaFormatter");
		}

		if (null != config.get("org.eclipse.jdt.core.javaFormatter")) {
			throw new CannotLoadConfigurationException("The use of third-party Java code formatters is not supported by this plugin.\n"
					+ "See https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77\n"
					+ "Try to remove the entry 'org.eclipse.jdt.core.javaFormatter' from the configuration.");
		}

		return config;
	}
}
