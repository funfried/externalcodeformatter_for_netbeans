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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.annotations.common.NonNull;
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
	private static final Logger log = Logger.getLogger(ConfigReader.class.getName());

	public static final String ATTRIBUTE_PROFILE_KIND = "kind";

	public static final String ATTRIBUTE_PROFILE_NAME = "name";

	public static final String ATTRIBUTE_SETTING_ID = "id";

	public static final String ATTRIBUTE_SETTING_VALUE = "value";

	public static final String TAG_NAME_PROFILES = "profiles";

	public static final String TAG_NAME_PROFILE = "profile";

	public static final String TAG_NAME_SETTING = "setting";

	public static final String PROFILE_KIND = "CodeFormatterProfile";

	/**
	 * Reads the content of the given file path and returns it as a {@link String}.
	 *
	 * @param filePath a file path
	 *
	 * @return the content of the file at the fiven {@code filePath}
	 *
	 * @throws IOException if there is an issue accessing the file at the given path
	 */
	@NonNull
	public static String readContentFromFilePath(String filePath) throws IOException {
		try {
			URL url = new URL(filePath);

			return IOUtils.toString(url.openStream(), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			log.log(Level.FINEST, "Could not read file via URL, fallback to local file reading", ex);

			return FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
		}
	}

	/**
	 * Parses and returns the key/value pairs from the given {@code fileContent} for the given {@code profileName} as a {@link Map}.
	 *
	 * @param fileContent the file content to parse
	 * @param profileName the profile name for which to get the settings
	 *
	 * @return a {@link Map} within all the configuration paramters of the given {@code profileName} read from the given {@code fileContent},
	 *         or throws an exception if there's a problem reading the input, e.g.: invalid XML.
	 *
	 * @throws SAXException if there are parsing issues
	 * @throws IOException if there is an I/O issue
	 * @throws ConfigReadException if the given {@code fileContent} is not a valid Eclipse formatter template
	 * @throws ProfileNotFoundException if no profile could be found with the given {@code profileName} in the given {@code fileContent}
	 */
	@NonNull
	public static Map<String, String> getProfileSettings(String fileContent, String profileName) throws ConfigReadException, ProfileNotFoundException, IOException, SAXException {
		List<Node> profileNodes = getProfileNodes(fileContent);
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

		throw new ProfileNotFoundException("Profile " + profileName + " not found in given file content");
	}

	/**
	 * Parses the given {@code fileContent} and returns a {@link List} within all profile names found in that {@code fileContent}.
	 *
	 * @param fileContent the file content to parse
	 *
	 * @return a {@link List} within all profile names found in the given {@code fileContent}
	 *
	 * @throws SAXException if there are parsing issues
	 * @throws IOException if there is an I/O issue
	 * @throws ConfigReadException if the given {@code fileContent} is not a valid Eclipse formatter template
	 */
	@NonNull
	public static List<String> getProfileNames(String fileContent) throws ConfigReadException, IOException, SAXException {
		List<String> profileNames = new ArrayList<>();

		List<Node> profileNodes = getProfileNodes(fileContent);
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
	 * Parses the given {@code fileContent} and returns a {@link List} within all profile {@link Node}s that were found in that {@code fileContent}.
	 *
	 * @param fileContent the file content to parse
	 *
	 * @return a {@link List} within all profile {@link Node}s that were found in the given {@code fileContent}
	 *
	 * @throws SAXException if there are parsing issues
	 * @throws IOException if there is an I/O issue
	 * @throws ConfigReadException if the given {@code fileContent} is not a valid Eclipse formatter template
	 */
	@NonNull
	private static List<Node> getProfileNodes(String fileContent) throws ConfigReadException, IOException, SAXException {
		if (fileContent == null) {
			throw new ConfigReadException("fileContent cannot be null");
		}

		List<Node> profiles = new ArrayList<>();

		Document formatterDoc;
		try (StringReader reader = new StringReader(fileContent)) {
			formatterDoc = XMLUtil.parse(new InputSource(reader), false, false, null, null);
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
				throw new ConfigReadException("No <profile> tag found in given file content");
			}
		} else {
			throw new ConfigReadException("No <profiles> tag found in config file");
		}

		if (profiles.isEmpty()) {
			throw new ConfigReadException("No valid <profile> tag found in given file content");
		}

		return profiles;
	}
}
