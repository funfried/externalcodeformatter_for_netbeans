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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;

import de.funfried.netbeans.plugins.external.formatter.eclipse.mechanic.WorkspaceMechanicConfigParser;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;

/**
 *
 * @author bahlef
 */
public class EclipseFormatterUtils {
	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(EclipseFormatterUtils.class.getName());

	/** EPF file extension */
	private static final String EPF_FILE_EXTENSION = ".epf";

	/** XML file extension */
	private static final String XML_FILE_EXTENSION = ".xml";

	/**
	 * Private constructor due to static methods only.
	 */
	private EclipseFormatterUtils() {
	}

	/**
	 * Returns the Eclipse formatter file for the given {@link Document} from the given {@link Preferences}.
	 * If the value behind {@code useProjectPrefsKey} is {@code true} in the given {@link Preferences}, it
	 * will be automatically checked if there is a project specific formatter configuration file available.
	 *
	 * @param preferences the {@link Preferences} where to load from
	 * @param document the {@link Document}
	 * @param configFileLocationKey the preferences key for the configuration file location
	 * @param useProjectPrefsKey the preferences key whether to use project preferences
	 * @param projectPrefFile the expected Eclipse project specific formatter configuration file name
	 *
	 * @return the Eclipse formatter file for the given {@link Document} from the given {@link Preferences}.
	 *         If the value behind {@code useProjectPrefsKey} is {@code true} in the given {@link Preferences},
	 *         it will be automatically checked if there is a project specific formatter configuration file
	 *         available.
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
	 * @param document the {@link Document}
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
	 * Returns {@code true} if the given {@code filename} ends with the given {@code projectPrefFile}.
	 *
	 * @param filename the filename to check
	 * @param projectPrefFile the expected Eclipse project specific formatter configuration file name
	 *
	 * @return {@code true} if the given {@code filename} ends with {@code org.eclipse.jdt.core.prefs},
	 *         otherwise {@code false}
	 */
	public static boolean isProjectSetting(String filename, String projectPrefFile) {
		return filename != null && StringUtils.isNotBlank(projectPrefFile) && filename.endsWith(projectPrefFile);
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
	 * Parses the configuration parameters from the given {@code formatterProfile} of the
	 * given {@code formatterFile} and returns it as a {@link Map} containing the
	 * configuration as key value pairs.
	 *
	 * @param formatterFile the path to the formatter configuration file
	 * @param formatterProfile the name of the formatter configuration profile
	 * @param defaultProperties the default properties
	 * @param additionalProperties optional additional properties
	 * @param workspaceMechanicPrefix the workspace mechanic prefix
	 * @param projectPrefFile the expected Eclipse project specific formatter configuration file name
	 *
	 * @return a {@link Map} containing the configuration as key value pairs
	 *
	 * @throws ConfigReadException if there is an issue parsing the formatter configuration
	 * @throws ProfileNotFoundException if the given {@code profile} could not be found
	 * @throws CannotLoadConfigurationException if there is any issue accessing or reading the formatter configuration
	 */
	public static Map<String, String> parseConfig(String formatterFile, String formatterProfile, Map<String, String> defaultProperties, Map<String, String> additionalProperties,
			String workspaceMechanicPrefix, String projectPrefFile) throws ProfileNotFoundException, ConfigReadException, CannotLoadConfigurationException {
		Map<String, String> allConfig = new HashMap<>();
		try {
			Map<String, String> configFromFile;
			if (EclipseFormatterUtils.isWorkspaceMechanicFile(formatterFile)) {
				configFromFile = WorkspaceMechanicConfigParser.readPropertiesFromConfigurationFile(formatterFile, workspaceMechanicPrefix);
			} else if (EclipseFormatterUtils.isXMLConfigurationFile(formatterFile)) {
				configFromFile = ConfigReader.getProfileSettings(ConfigReader.readContentFromFilePath(formatterFile), formatterProfile);
			} else if (EclipseFormatterUtils.isProjectSetting(formatterFile, projectPrefFile)) {
				configFromFile = EclipseFormatterUtils.readPropertiesFromConfigurationFile(formatterFile);
			} else {
				configFromFile = new LinkedHashMap<>();
			}

			allConfig.putAll(defaultProperties);
			allConfig.putAll(configFromFile);

			if (additionalProperties != null) {
				allConfig.putAll(additionalProperties);
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
	 * Parses and returns properties of the given {@code filePath} into a key value {@link Map}. If an optional
	 * {@code prefix} is specified, only the properties where the key starts with the given {@code prefix}
	 * are returned and the {@code prefix} will be removed from the keys in the returned {@link Map}.
	 *
	 * @param filePath a configuration file path
	 * @param prefix an optional key prefix
	 *
	 * @return properties of the given {@code file} as a key value {@link Map}
	 *
	 * @throws IOException if there is an issue accessing the given configuration file
	 */
	@NonNull
	public static Map<String, String> readPropertiesFromConfigurationFile(String filePath) throws IOException {
		Properties properties = new Properties();

		try {
			URL url = new URL(filePath);

			properties.load(url.openStream());
		} catch (IOException ex) {
			log.log(Level.FINEST, "Could not read file via URL, fallback to local file reading", ex);

			try (FileInputStream is = new FileInputStream(filePath)) {
				properties.load(is);
			}
		}

		return EclipseFormatterUtils.toMap(properties, null);
	}

	/**
	 * Collect the given properties into a map and optionall filter the property keys by the given optional prefix.
	 *
	 * @param properties The {@link Properties} to filter and collect.
	 * @param prefix An optional prefix to filter the keys.
	 *
	 * @return A map containing the keys and their respective values
	 */
	public static Map<String, String> toMap(Properties properties, String prefix) {
		Stream<Object> stream = properties.keySet().stream();
		if (StringUtils.isNotBlank(prefix)) {
			return stream.filter(key -> ((String) key).startsWith(prefix)).collect(Collectors.toMap(key -> ((String) key).substring(prefix.length()), key -> properties.getProperty((String) key)));
		}

		return stream.collect(Collectors.toMap(key -> (String) key, key -> properties.getProperty((String) key)));
	}
}
