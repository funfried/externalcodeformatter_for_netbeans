/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui.options;

import java.util.prefs.Preferences;

import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbPreferences;

/**
 * Settings utility class.
 *
 * @author markiewb
 * @author bahlef
 */
public class Settings {
	/**
	 * Property key which defines the enabled formatter.
	 *
	 * @since 1.13
	 */
	public static final String ENABLED_FORMATTER = "enabledFormatter";

	/**
	 * Property value of the default formatter to use (NetBeans internal formatter).
	 *
	 * @since 1.13
	 */
	public static final String DEFAULT_FORMATTER = "netbeans-formatter";

	/** Property key which defines the active profile of the Eclipse configuration file. */
	public static final String ECLIPSE_FORMATTER_ACTIVE_PROFILE = "eclipseFormatterActiveProfile";

	/** Property key which defines the location of the Eclipse formatter configuration file. */
	public static final String ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION = "eclipseFormatterLocation";

	/** Property key which defines whether or not to use the settings of the external formatter in the NetBeans editor. */
	public static final String ENABLE_USE_OF_INDENTATION_SETTINGS = "enableIndentationSettings";

	/** Property key which defines whether or not to use the {@link #OVERRIDE_TAB_SIZE_VALUE} instead of the one inside the external formatter configuration. */
	public static final String OVERRIDE_TAB_SIZE = "overrideTabSize";

	/** Property key which defines the tab size which is used when {@link #OVERRIDE_TAB_SIZE} is actived. */
	public static final String OVERRIDE_TAB_SIZE_VALUE = "overrideTabSizeValue";

	/** Property key which defines whether or not to show notifications after each formatting. */
	public static final String SHOW_NOTIFICATIONS = "showNotifications";

	/** Property key which defines whether or not to use project specific settings instead of global formatter settings. */
	public static final String USE_PROJECT_SETTINGS = "useProjectSettings";

	/**
	 * Property key which defines whether or not to use Eclipse project specific formatter configuration if available.
	 *
	 * @since 1.10
	 */
	public static final String USE_PROJECT_PREFS = "useProjectPref";

	/**
	 * Constant value of the Eclipse project specific formatter configuration file name.
	 *
	 * @since 1.10
	 */
	public static final String PROJECT_PREF_FILE = "org.eclipse.jdt.core.prefs";

	/**
	 * Property key which defines the line feed setting for the Eclipse formatter.
	 *
	 * @since 1.10
	 */
	public static final String LINEFEED = "linefeed";

	/**
	 * Property key which defines the source level setting for the Eclipse formatter.
	 *
	 * @since 1.10
	 */
	public static final String SOURCELEVEL = "sourcelevel";

	/**
	 * Property key which defines which code style should be used for the Google formatter.
	 *
	 * @since 1.13
	 */
	public static final String GOOGLE_FORMATTER_CODE_STYLE = "googleFormatterCodeStyle";

	/**
	 * Private contructor because of static methods only.
	 */
	private Settings() {
	}

	/**
	 * Returns the active {@link Preferences} object for the given {@link Document}, either the global
	 * preferences are returned or if the {@link Project} has a separate configuration it will return
	 * the project specific {@link Preferences}.
	 *
	 * @param document the document to get the {@link Preferences} for
	 *
	 * @return the active {@link Preferences} object for the given {@link Document}, either the global
	 *         preferences are returned or if the {@link Project} has a separate configuration it will return
	 *         the project specific {@link Preferences}
	 */
	public static Preferences getActivePreferences(Document document) {
		Preferences globalPreferences = NbPreferences.forModule(ExternalFormatterPanel.class);
		if (document != null) {
			DataObject dataObj = NbEditorUtilities.getDataObject(document);
			if (dataObj != null) {
				FileObject primaryFile = dataObj.getPrimaryFile();
				if (primaryFile != null) {
					Project project = FileOwnerQuery.getOwner(primaryFile);
					if (null != project) {
						Preferences projectPreferences = ProjectUtils.getPreferences(project, ExternalFormatterPanel.class, true);
						if (projectPreferences.getBoolean(USE_PROJECT_SETTINGS, false)) {
							return projectPreferences;
						}
					}
				}
			}
		}

		return globalPreferences;
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
		String formatterFilePref = null;
		if (preferences.getBoolean(Settings.USE_PROJECT_PREFS, true)) {
			//use ${projectdir}/.settings/org.eclipse.jdt.core.prefs, if activated in options
			formatterFilePref = getFormatterFileFromProjectConfiguration(document);
		}

		if (StringUtils.isBlank(formatterFilePref)) {
			formatterFilePref = preferences.get(Settings.ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION, null);
		}

		return formatterFilePref;
	}

	/**
	 * Checks for a project specific Eclipse formatter configuration for the given {@link Document} and returns
	 * the file location if found, otherwise {@code null}.
	 *
	 * @param document the {@link Document}
	 *
	 * @return project specific Eclipse formatter configuration for the given {@link Document} if existent,
	 *         otherwise {@code null}
	 */
	@CheckForNull
	private static String getFormatterFileFromProjectConfiguration(Document document) {
		FileObject fileForDocument = NbEditorUtilities.getFileObject(document);
		if (null != fileForDocument) {

			Project project = FileOwnerQuery.getOwner(fileForDocument);
			if (null != project) {
				FileObject projectDirectory = project.getProjectDirectory();
				FileObject preferenceFile = projectDirectory.getFileObject(".settings/" + Settings.PROJECT_PREF_FILE);
				if (null != preferenceFile) {
					return preferenceFile.getPath();
				}
			}
		}

		return null;
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
		return filename != null && filename.endsWith("epf");
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
		return filename != null && filename.endsWith("xml");
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
		return filename != null && filename.endsWith(PROJECT_PREF_FILE);
	}

	/**
	 * Returns the real line feed characters for the given escaped line feed characters.
	 *
	 * @param lineFeedSetting escaped line feed characters, e.g. {@code \\n}
	 * @param fallback        if the escaped line feed characters could not be matched to a real line feed setting
	 *
	 * @return the real line feed characters for the given escaped line feed characters, or the given
	 *         {@code fallback} if the escaped characters could not be matched to a real line feed setting
	 */
	public static String getLineFeed(String lineFeedSetting, String fallback) {
		String linefeed = fallback;

		boolean usePlatformLinefeed = StringUtils.isBlank(lineFeedSetting);
		if (!usePlatformLinefeed) {
			switch (lineFeedSetting) {
				case "\\n":
					linefeed = BaseDocument.LS_LF;
					break;
				case "\\r":
					linefeed = BaseDocument.LS_CR;
					break;
				case "\\r\\n":
					linefeed = BaseDocument.LS_CRLF;
					break;
				default:
					linefeed = null;
					break;
			}
		}

		return linefeed;
	}
}
