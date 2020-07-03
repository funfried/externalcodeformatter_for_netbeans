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

import java.util.Map;

import org.eclipse.wst.jsdt.core.formatter.DefaultCodeFormatterConstants;

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
	private static final String WORKSPACE_MECHANIC_PREFIX = "/instance/org.eclipse.wst.jsdt.core/";

	/** Default configuration of the Eclipse Javascript formatter. */
	@SuppressWarnings("unchecked")
	private static final Map<String, String> ECLIPSE_JAVASCRIPT_FORMATTER_DEFAULTS = DefaultCodeFormatterConstants.getJSLintConventionsSettings();

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
	 *
	 * @return a {@link Map} containing the configuration as key value pairs
	 *
	 * @throws ConfigReadException              if there is an issue parsing the formatter configuration
	 * @throws ProfileNotFoundException         if the given {@code profile} could not be found
	 * @throws CannotLoadConfigurationException if there is any issue accessing or reading the formatter configuration
	 */
	public static Map<String, String> parseConfig(String formatterFile, String formatterProfile)
			throws ProfileNotFoundException, ConfigReadException, CannotLoadConfigurationException {
		return EclipseFormatterUtils.parseConfig(formatterFile, formatterProfile, ECLIPSE_JAVASCRIPT_FORMATTER_DEFAULTS, null, WORKSPACE_MECHANIC_PREFIX,
				EclipseJavascriptFormatterSettings.PROJECT_PREF_FILE);
	}
}
