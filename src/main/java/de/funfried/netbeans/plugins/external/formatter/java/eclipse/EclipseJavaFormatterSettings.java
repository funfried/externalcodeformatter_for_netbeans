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

import java.util.prefs.Preferences;

import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;

/**
 * Utility class for Eclipse specific settings.
 *
 * @author bahlef
 */
public class EclipseJavaFormatterSettings {
	/** Property key which defines the active profile of the Eclipse configuration file. */
	public static final String ECLIPSE_FORMATTER_ACTIVE_PROFILE = "eclipseFormatterActiveProfile";

	/** Property key which defines the location of the Eclipse formatter configuration file. */
	public static final String ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION = "eclipseFormatterLocation";

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
	 * Private contructor because of static methods only.
	 */
	private EclipseJavaFormatterSettings() {
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
		if (preferences.getBoolean(EclipseJavaFormatterSettings.USE_PROJECT_PREFS, true)) {
			//use ${projectdir}/.settings/org.eclipse.jdt.core.prefs, if activated in options
			formatterFilePref = getFormatterFileFromProjectConfiguration(document);
		}

		if (StringUtils.isBlank(formatterFilePref)) {
			formatterFilePref = preferences.get(EclipseJavaFormatterSettings.ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION, null);
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
				FileObject preferenceFile = projectDirectory.getFileObject(".settings/" + EclipseJavaFormatterSettings.PROJECT_PREF_FILE);
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
}
