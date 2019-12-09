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

import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbPreferences;

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

	public static Preferences getActivePreferences(final StyledDocument styledDoc) {
		Preferences globalPreferences = NbPreferences.forModule(ExternalFormatterPanel.class);
		DataObject dataObj = NbEditorUtilities.getDataObject(styledDoc);
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
}
