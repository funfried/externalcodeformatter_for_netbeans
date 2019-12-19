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

import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;

import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbPreferences;

import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.EclipseFormatterConfig;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class Settings {
	public static final boolean FEATURE_FORMAT_CHANGED_LINES_ONLY = true;

	public static final String ECLIPSE_FORMATTER_ACTIVE_PROFILE = "eclipseFormatterActiveProfile";

	public static final String ECLIPSE_FORMATTER_ENABLED = "eclipseFormatterEnabled";

	public static final String ECLIPSE_FORMATTER_LOCATION = "eclipseFormatterLocation";

	public static final String ENABLE_SAVEACTION = "enableFormatAsSaveAction";

	public static final String ENABLE_USE_OF_INDENTATION_SETTINGS = "enableIndentationSettings";

	public static final String OVERRIDE_TAB_SIZE = "overrideTabSize";

	public static final String OVERRIDE_TAB_SIZE_VALUE = "overrideTabSizeValue";

	public static final String ENABLE_SAVEACTION_MODIFIEDLINESONLY = "SaveActionModifiedLinesOnly";

	/**
	 * @since 1.8
	 */
	public static final String PRESERVE_BREAKPOINTS = "preserveBreakPoints";

	public static final String SHOW_NOTIFICATIONS = "showNotifications";

	public static final String USE_PROJECT_SETTINGS = "useProjectSettings";

	/**
	 * @since 1.10
	 */
	public static final String USE_PROJECT_PREFS = "useProjectPref";

	/**
	 * @since 1.10
	 */
	public static final String PROJECT_PREF_FILE = "org.eclipse.jdt.core.prefs";

	/**
	 * @since 1.10
	 */
	public static final String LINEFEED = "linefeed";

	/**
	 * @since 1.10
	 */
	public static final String SOURCELEVEL = "sourcelevel";

	/**
	 * @since 1.13
	 */
	public static final String GOOGLE_FORMATTER_ENABLED = "googleFormatterEnabled";

	/**
	 * @since 1.13
	 */
	public static final String GOOGLE_FORMATTER_CODE_STYLE = "googleFormatterCodeStyle";

	public static Preferences getActivePreferences(Document document) {
		Preferences globalPreferences = NbPreferences.forModule(ExternalFormatterPanel.class);
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

		return globalPreferences;
	}

	public static String getEclipseFormatterFile(Preferences preferences, Document document) {
		String formatterFilePref = getFormatterFileFromProjectConfiguration(preferences.getBoolean(Settings.USE_PROJECT_PREFS, true), document);
		if (null == formatterFilePref) {
			formatterFilePref = preferences.get(Settings.ECLIPSE_FORMATTER_LOCATION, null);
		}

		return formatterFilePref;
	}

	private static String getFormatterFileFromProjectConfiguration(boolean useProjectPrefs, Document document) {
		//use ${projectdir}/.settings/org.eclipse.jdt.core.prefs, if activated in options
		if (useProjectPrefs) {
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
		}

		return null;
	}

	public static boolean isWorkspaceMechanicFile(String filename) {
		return filename != null && filename.endsWith("epf");
	}

	public static boolean isXMLConfigurationFile(String filename) {
		return filename != null && filename.endsWith("xml");
	}

	public static boolean isProjectSetting(String filename) {
		return filename != null && filename.endsWith(PROJECT_PREF_FILE);
	}

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

	public static boolean isUseFormatterIndentationSettings(Document document) {
		if (document == null) {
			return false;
		}

		Preferences preferences = getActivePreferences(document);
		if (preferences.getBoolean(ECLIPSE_FORMATTER_ENABLED, true) || preferences.getBoolean(GOOGLE_FORMATTER_ENABLED, true)) {
			return preferences.getBoolean(ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		}

		return false;
	}

	public static Integer getRightMargin(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = getActivePreferences(document);
		if (preferences.getBoolean(ECLIPSE_FORMATTER_ENABLED, true)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.lineSplit");
			if (value != null) {
				ret = Integer.valueOf(value);
			}
		} else if (preferences.getBoolean(GOOGLE_FORMATTER_ENABLED, true)) {
			// see: https://google.github.io/styleguide/javaguide.html#s4.4-column-limit
			ret = 100;
		}

		return ret;
	}

	public static Boolean isExpandTabToSpaces(Document document) {
		if (document == null || !isUseFormatterIndentationSettings(document)) {
			return null;
		}

		Boolean ret = null;

		Preferences preferences = getActivePreferences(document);
		if (preferences.getBoolean(ECLIPSE_FORMATTER_ENABLED, true)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.tabulation.char");
			if (value != null) {
				ret = Objects.equals(value, "space");
			}
		} else if (preferences.getBoolean(GOOGLE_FORMATTER_ENABLED, true)) {
			// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
			ret = false;
		}

		return ret;
	}

	public static Integer getSpacesPerTab(Document document) {
		if (document == null || !isUseFormatterIndentationSettings(document)) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = getActivePreferences(document);
		if (preferences.getBoolean(OVERRIDE_TAB_SIZE, true)) {
			ret = preferences.getInt(OVERRIDE_TAB_SIZE_VALUE, 4);
		} else if (preferences.getBoolean(ECLIPSE_FORMATTER_ENABLED, true)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.tabulation.size");
			if (value != null) {
				ret = Integer.valueOf(value);
			}
		} else if (preferences.getBoolean(GOOGLE_FORMATTER_ENABLED, true)) {
			// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
			ret = 2;
		}

		return ret;
	}

	public static Integer getIndentSize(Document document) {
		if (document == null || !isUseFormatterIndentationSettings(document)) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = getActivePreferences(document);
		if (preferences.getBoolean(ECLIPSE_FORMATTER_ENABLED, true)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.indentation.size");
			if (value != null) {
				ret = Integer.valueOf(value);
			}
		} else if (preferences.getBoolean(GOOGLE_FORMATTER_ENABLED, true)) {
			// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
			ret = 2;
		}

		return ret;
	}

	public static Integer getContinuationIndentSize(Document document) {
		if (document == null || !isUseFormatterIndentationSettings(document)) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (preferences.getBoolean(Settings.ECLIPSE_FORMATTER_ENABLED, true)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.continuation_indentation");
			if (value != null) {
				ret = Integer.valueOf(value);
			}
		} else if (preferences.getBoolean(GOOGLE_FORMATTER_ENABLED, true)) {
			// see: https://google.github.io/styleguide/javaguide.html#s4.5.2-line-wrapping-indent
			ret = 4;
		}

		return ret;
	}

	private static String getEclipseFormatterProperty(Preferences preferences, Document document, String key) {
		if (preferences == null || document == null) {
			return null;
		}

		String formatterFile = Settings.getEclipseFormatterFile(preferences, document);
		String formatterProfile = preferences.get(Settings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
		String sourceLevel = preferences.get(Settings.SOURCELEVEL, "");

		Map<String, String> config = EclipseFormatterConfig.parseConfig(formatterFile, formatterProfile, sourceLevel);

		return config.getOrDefault(key, null);
	}
}
