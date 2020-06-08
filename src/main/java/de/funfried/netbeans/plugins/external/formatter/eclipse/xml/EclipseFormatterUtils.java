/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter.eclipse.xml;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;

/**
 *
 * @author bahlef
 */
public class EclipseFormatterUtils {
	/** EPF file extension */
	private static final String EPF_FILE_EXTENSION = ".epf";

	/** XML file extension */
	private static final String XML_FILE_EXTENSION = ".xml";

	/**
	 * Returns the Eclipse formatter file for the given {@link Document} from the given {@link Preferences}.
	 * If {@link #USE_PROJECT_PREFS} is {@code true} in the given {@link Preferences}, it will be automatically
	 * checked if there is a project specific formatter configuration file available.
	 *
	 * @param preferences           the {@link Preferences} where to load from
	 * @param document              the {@link Document}
	 * @param configFileLocationKey the preferences key for the configuration file location
	 * @param useProjectPrefsKey    the preferences key whether to use project preferences
	 * @param projectPrefFile       the expected Eclipse project specific formatter configuration file name
	 *
	 * @return the Eclipse formatter file for the given {@link Document} from the given {@link Preferences}.
	 *         If {@link #USE_PROJECT_PREFS} is {@code true} in the given {@link Preferences}, it will be automatically
	 *         checked if there is a project specific formatter configuration file available
	 */
	public static String getEclipseFormatterFile(Preferences preferences, Document document, String configFileLocationKey, String useProjectPrefsKey, String projectPrefFile) {
		String formatterFilePref = null;
		if (preferences.getBoolean(useProjectPrefsKey, true)) {
			//use ${projectdir}/.settings/projectPrefFile, if activated in options
			formatterFilePref = getFormatterFileFromProjectConfiguration(document, ".settings/" + projectPrefFile);
		}

		if (StringUtils.isBlank(formatterFilePref)) {
			formatterFilePref = preferences.get(configFileLocationKey, null);
			if (StringUtils.isNotBlank(formatterFilePref)) {
				Path formatterFilePath = Paths.get(formatterFilePref);
				if (!formatterFilePath.isAbsolute()) {
					formatterFilePref = getFormatterFileFromProjectConfiguration(document, formatterFilePref);
				}
			}
		}

		return formatterFilePref;
	}

	/**
	 * Checks for a project specific Eclipse formatter configuration for the given {@link Document} and returns
	 * the file location if found, otherwise {@code null}.
	 *
	 * @param document         the {@link Document}
	 * @param relativeFileName the relative configuration file name
	 *
	 * @return project specific Eclipse formatter configuration for the given {@link Document} if existent,
	 *         otherwise {@code null}
	 */
	@CheckForNull
	private static String getFormatterFileFromProjectConfiguration(Document document, String relativeFileName) {
		FileObject fileForDocument = NbEditorUtilities.getFileObject(document);
		if (null != fileForDocument) {
			Project project = FileOwnerQuery.getOwner(fileForDocument);
			if (null != project) {
				FileObject projectDirectory = project.getProjectDirectory();
				FileObject preferenceFile = projectDirectory.getFileObject(StringUtils.replace(relativeFileName, "\\", "/"));
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
		return filename != null && filename.endsWith(EPF_FILE_EXTENSION);
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
		return filename != null && filename.endsWith(XML_FILE_EXTENSION);
	}

	/**
	 * Returns {@code true} if the given {@code filename} ends with the given {@code projectPrefFile}.
	 *
	 * @param filename        the filename to check
	 * @param projectPrefFile the expected Eclipse project specific formatter configuration file name
	 *
	 * @return {@code true} if the given {@code filename} ends with {@code org.eclipse.jdt.core.prefs},
	 *         otherwise {@code false}
	 */
	public static boolean isProjectSetting(String filename, String projectPrefFile) {
		return filename != null && StringUtils.isNotBlank(projectPrefFile) && filename.endsWith(projectPrefFile);
	}
}
