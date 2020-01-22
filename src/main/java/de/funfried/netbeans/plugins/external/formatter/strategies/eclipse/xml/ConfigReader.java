/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;

/**
 * This class reads a config file for Eclipse code formatter.
 *
 * @author bahlef
 */
public class ConfigReader {
	public static final String ATTRIBUTE_PROFILE_KIND = "kind";

	public static final String ATTRIBUTE_PROFILE_NAME = "name";

	public static final String ATTRIBUTE_SETTING_ID = "id";

	public static final String ATTRIBUTE_SETTING_VALUE = "value";

	public static final String TAG_NAME_PROFILES = "profiles";

	public static final String TAG_NAME_PROFILE = "profile";

	public static final String TAG_NAME_SETTING = "setting";

	public static final String PROFILE_KIND = "CodeFormatterProfile";

	/**
	 * Creates a {@link FileObject} from the given {@code filePath} and returns it.
	 *
	 * @param filePath the path to a file which should be turned into a {@link FileObject}
	 *
	 * @return a {@link FileObject} representing the given {@code filePath}
	 *
	 * @throws IOException if there is an I/O issue while trying to access the given {@code filePath}
	 */
	public static FileObject toFileObject(String filePath) throws IOException {
		if (StringUtils.isBlank(filePath)) {
			throw new IOException("File path cannot be empty");
		}

		return ConfigReader.toFileObject(new File(filePath));
	}

	/**
	 * Creates a {@link FileObject} from the given {@link File} and returns it.
	 *
	 * @param file the {@link File} which should be turned into a {@link FileObject}
	 *
	 * @return a {@link FileObject} representing the given {@link File}
	 *
	 * @throws IOException if there is an I/O issue while trying to access the given {@link File}
	 */
	public static FileObject toFileObject(File file) throws IOException {
		if (file == null || !file.exists() || !file.canRead()) {
			throw new IOException("Could not access file: " + (file != null ? file.getAbsolutePath() : null));
		}

		file = FileUtil.normalizeFile(file);

		return FileUtil.toFileObject(file);
	}

	/**
	 * Parses and returns the key/value pairs from the given {@link FileObject} for the given {@code profileName} as a {@link Map}.
	 *
	 * @param fileObject  the {@link FileObject} to parse
	 * @param profileName the profile name for which to get the settings
	 *
	 * @return a {@link Map} within all the configuration paramters of the given {@code profileName} read from the given {@link FileObject},
	 *         or throws an exception if there's a problem reading the input, e.g.: invalid XML.
	 *
	 * @throws SAXException             if there are parsing issues
	 * @throws IOException              if there is an I/O issue
	 * @throws ConfigReadException      if the given {@link FileObject} is not a valid Eclipse formatter template
	 * @throws ProfileNotFoundException if no profile could be found with the given {@code profileName} in the given {@link FileObject}
	 */
	@NotNull
	public static Map<String, String> getProfileSettings(FileObject fileObject, String profileName) throws ConfigReadException, ProfileNotFoundException, IOException, SAXException {
		List<Node> profileNodes = getProfileNodes(fileObject);
		for (Node profileTag : profileNodes) {
			Node profileNameAttr = profileTag.getAttributes().getNamedItem(ATTRIBUTE_PROFILE_NAME);
			if (Objects.equals(profileName, profileNameAttr.getNodeValue())) {
				NodeList settingTagList = profileTag.getChildNodes();
				Map<String, String> config = new HashMap<>();
				for (int s = 0; s < settingTagList.getLength(); s++) {
					Node settingTag = settingTagList.item(s);
					if (TAG_NAME_SETTING.equals(settingTag.getNodeName())) {
						NamedNodeMap attributes = settingTag.getAttributes();
						Node keyAttr = attributes.getNamedItem(ATTRIBUTE_SETTING_ID);
						if (keyAttr != null) {
							Node valueAttr = attributes.getNamedItem(ATTRIBUTE_SETTING_VALUE);
							if (valueAttr != null) {
								config.put(keyAttr.getNodeValue(), valueAttr.getNodeValue());
							}
						}
					}
				}

				return config;
			}
		}

		throw new ProfileNotFoundException("Profile " + profileName + " not found in " + fileObject.getPath());
	}

	/**
	 * Parses the given {@link FileObject} and returns a {@link List} within all profile names found in that {@link FileObject}.
	 *
	 * @param fileObject the {@link FileObject} to parse
	 *
	 * @return a {@link List} within all profile names found in the given {@link FileObject}
	 *
	 * @throws SAXException        if there are parsing issues
	 * @throws IOException         if there is an I/O issue
	 * @throws ConfigReadException if the given {@link FileObject} is not a valid Eclipse formatter template
	 */
	@NotNull
	public static List<String> getProfileNames(FileObject fileObject) throws ConfigReadException, IOException, SAXException {
		List<String> profileNames = new ArrayList<>();

		List<Node> profileNodes = getProfileNodes(fileObject);
		for (Node profileTag : profileNodes) {
			Node profileNameAttr = profileTag.getAttributes().getNamedItem(ATTRIBUTE_PROFILE_NAME);
			if (profileNameAttr != null) {
				String profileName = profileNameAttr.getNodeValue();
				if (StringUtils.isNotBlank(profileName)) {
					profileNames.add(profileNameAttr.getNodeValue());
				}
			}
		}

		return profileNames;
	}

	/**
	 * Parses the given {@link FileObject} and returns a {@link List} within all profile {@link Node}s that were found in that {@link FileObject}.
	 *
	 * @param fileObject the {@link FileObject} to parse
	 *
	 * @return a {@link List} within all profile {@link Node}s that were found in the given {@link FileObject}
	 *
	 * @throws SAXException        if there are parsing issues
	 * @throws IOException         if there is an I/O issue
	 * @throws ConfigReadException if the given {@link FileObject} is not a valid Eclipse formatter template
	 */
	@NotNull
	private static List<Node> getProfileNodes(FileObject fileObject) throws ConfigReadException, IOException, SAXException {
		List<Node> profiles = new ArrayList<>();

		Document formatterDoc;
		try (InputStream is = fileObject.getInputStream()) {
			formatterDoc = XMLUtil.parse(new InputSource(is), false, false, null, null);
		}

		Element profilesTag = formatterDoc.getDocumentElement();
		if (profilesTag != null && TAG_NAME_PROFILES.equals(profilesTag.getNodeName())) {
			NodeList profileTagList = profilesTag.getElementsByTagName(TAG_NAME_PROFILE);
			if (profileTagList != null && profileTagList.getLength() > 0) {
				for (int p = 0; p < profileTagList.getLength(); p++) {
					Node profileTag = profileTagList.item(p);
					NamedNodeMap profileAttributes = profileTag.getAttributes();
					Node profileKindAttr = profileAttributes.getNamedItem(ATTRIBUTE_PROFILE_KIND);
					if (profileKindAttr != null && PROFILE_KIND.equals(profileKindAttr.getNodeValue())) {
						profiles.add(profileTag);
					}
				}
			} else {
				throw new ConfigReadException("No <profile> tag found in " + fileObject.getPath());
			}
		} else {
			throw new ConfigReadException("No <profiles> tag found in config file");
		}

		if (profiles.isEmpty()) {
			throw new ConfigReadException("No valid <profile> tag found in " + fileObject.getPath());
		}

		return profiles;
	}
}
