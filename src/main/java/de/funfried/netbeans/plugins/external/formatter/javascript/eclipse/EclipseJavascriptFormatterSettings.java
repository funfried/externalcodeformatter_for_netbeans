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

import java.util.prefs.Preferences;

import javax.swing.text.Document;

import de.funfried.netbeans.plugins.external.formatter.eclipse.xml.EclipseFormatterUtils;

/**
 * Utility class for Eclipse specific settings.
 *
 * @author bahlef
 */
public class EclipseJavascriptFormatterSettings {
	/**
	 * Property key which defines the active profile of the Eclipse configuration file.
	 *
	 * @since 1.14
	 */
	public static final String ECLIPSE_FORMATTER_ACTIVE_PROFILE = "eclipseJsFormatterActiveProfile";

	/**
	 * Property key which defines the location of the Eclipse formatter configuration file.
	 *
	 * @since 1.14
	 */
	public static final String ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION = "eclipseJsFormatterLocation";

	/**
	 * Property key which defines whether or not to use Eclipse project specific formatter configuration if available.
	 *
	 * @since 1.14
	 */
	public static final String USE_PROJECT_PREFS = "useJsProjectPref";

	/**
	 * Constant value of the Eclipse project specific formatter configuration file name.
	 *
	 * @since 1.14
	 */
	public static final String PROJECT_PREF_FILE = "org.eclipse.wst.jsdt.core.prefs";

	/**
	 * Property key which defines the line feed setting for the Eclipse formatter.
	 *
	 * @since 1.14
	 */
	public static final String LINEFEED = "jsLinefeed";

	/**
	 * Private contructor because of static methods only.
	 */
	private EclipseJavascriptFormatterSettings() {
	}

	/**
	 * Returns the Eclipse formatter file for the given {@link Document} from the given {@link Preferences}.
	 * If {@link #USE_PROJECT_PREFS} is {@code true} in the given {@link Preferences}, it will be automatically
	 * checked if there is a project specific formatter configuration file available.
	 *
	 * @param preferences the {@link Preferences} where to load from
	 * @param document    the {@link Document}
	 *
	 * @return the Eclipse formatter file for the given {@link Document} from the given {@link Preferences}.
	 *         If {@link #USE_PROJECT_PREFS} is {@code true} in the given {@link Preferences}, it will be automatically
	 *         checked if there is a project specific formatter configuration file available
	 */
	public static String getEclipseFormatterFile(Preferences preferences, Document document) {
		return EclipseFormatterUtils.getEclipseFormatterFile(preferences, document, ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION, USE_PROJECT_PREFS, PROJECT_PREF_FILE);
	}

	/**
	 * Returns {@code true} if the given {@code filename} ends with the workspace mechanic file extension epf.
	 *
	 * @param filename the filename to check
	 *
	 * @return {@code true} if the given {@code filename} ends with the workspace mechanic file extension epf,
	 *         otherwise {@code false}
	 */
	public static boolean isWorkspaceMechanicFile(String filename) {
		return EclipseFormatterUtils.isWorkspaceMechanicFile(filename);
	}

	/**
	 * Returns {@code true} if the given {@code filename} ends with the XML file extension.
	 *
	 * @param filename the filename to check
	 *
	 * @return {@code true} if the given {@code filename} ends with the XML file extension, otherwise
	 *         {@code false}
	 */
	public static boolean isXMLConfigurationFile(String filename) {
		return EclipseFormatterUtils.isXMLConfigurationFile(filename);
	}

	/**
	 * Returns {@code true} if the given {@code filename} ends with {@code org.eclipse.jdt.core.prefs}.
	 *
	 * @param filename the filename to check
	 *
	 * @return {@code true} if the given {@code filename} ends with {@code org.eclipse.jdt.core.prefs},
	 *         otherwise {@code false}
	 */
	public static boolean isProjectSetting(String filename) {
		return EclipseFormatterUtils.isProjectSetting(filename, PROJECT_PREF_FILE);
	}
}
