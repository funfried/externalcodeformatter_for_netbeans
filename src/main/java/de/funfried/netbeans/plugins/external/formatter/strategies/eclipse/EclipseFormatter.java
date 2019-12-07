/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * Saad Mufti <saad.mufti@teamaol.com>
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.TextEdit;
import org.openide.filesystems.FileUtil;
import org.xml.sax.SAXException;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml.ConfigReader;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml.Profile;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

public final class EclipseFormatter {
	private static final Logger log = Logger.getLogger(EclipseFormatter.class.getName());

	private static final int FORMATTER_OPTS = CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS /* + CodeFormatter.K_CLASS_BODY_DECLARATIONS + CodeFormatter.K_STATEMENTS */;

	private static final String WORKSPACE_MECHANIC_PREFIX = "/instance/org.eclipse.jdt.core/";

	@SuppressWarnings("unchecked")
	private static final Map<String, String> ECLIPSE_JAVA_FORMATTER_DEFAULTS = DefaultCodeFormatterConstants.getJavaConventionsSettings();

	private static final Map<String, String> SOURCE_LEVEL_DEFAULTS = new LinkedHashMap<>();

	static {
		String level = JavaCore.VERSION_1_6;
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_COMPLIANCE, level);
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
		SOURCE_LEVEL_DEFAULTS.put(JavaCore.COMPILER_SOURCE, level);
	}

	EclipseFormatter() {
	}

	public String format(String formatterFile, String formatterProfile, String code, String lineFeed, String sourceLevel, SortedSet<Pair<Integer, Integer>> changedElements)
			throws ConfigReadException, ProfileNotFoundException, CannotLoadConfigurationException {
		if (code == null) {
			return null;
		}

		Map<String, String> allConfig = readConfig(formatterFile, formatterProfile, sourceLevel);

		CodeFormatter formatter = ToolFactory.createCodeFormatter(allConfig, ToolFactory.M_FORMAT_EXISTING);
		//see http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fcore%2Fformatter%2FCodeFormatter.html&anchor=format(int,

		List<IRegion> regions = new ArrayList<>();
		if (!CollectionUtils.isEmpty(changedElements)) {
			for (Pair<Integer, Integer> changedElement : changedElements) {
				regions.add(new Region(changedElement.getLeft(), (changedElement.getRight() - changedElement.getLeft()) + 1));
			}
		} else {
			regions.add(new Region(0, code.length()));
		}

		return format(formatter, code, regions.toArray(new IRegion[regions.size()]), lineFeed);
	}

	private String format(CodeFormatter formatter, String code, IRegion[] regions, String lineFeed) {
		String formattedCode = null;

		TextEdit te = formatter.format(FORMATTER_OPTS, code, regions, 0, lineFeed);
		if (te != null && te.getChildrenSize() > 0) {
			try {
				IDocument dc = new Document(code);
				te.apply(dc);

				formattedCode = dc.get();

				if (Objects.equals(code, formattedCode)) {
					return null;
				}
			} catch (Exception ex) {
				log.log(Level.WARNING, "Code could not be formatted!", ex);
				return null;
			}
		}

		return formattedCode;
	}

	private Map<String, String> getSourceLevelOptions(String sourceLevel) {
		Map<String, String> options = new HashMap<>();
		if (null != sourceLevel && !"".equals(sourceLevel)) {
			String level = sourceLevel;
			options.put(JavaCore.COMPILER_COMPLIANCE, level);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
			options.put(JavaCore.COMPILER_SOURCE, level);
		}

		return options;
	}

	/**
	 *
	 * @return profile of <code>null</code> if profile with name not found
	 */
	private Profile getProfileByName(List<Profile> profiles, String name) {
		if (null == name) {
			return null;
		}
		for (Profile profile : profiles) {
			if (null != profile && name.equals(profile.getName())) {
				return profile;
			}
		}
		return null;
	}

	private Map<String, String> readConfig(String formatterFile, String formatterProfile, String sourceLevel) throws ProfileNotFoundException, ConfigReadException, CannotLoadConfigurationException {
		Map<String, String> allConfig = new HashMap<>();
		try {
			Map<String, String> configFromFile;
			if (Settings.isWorkspaceMechanicFile(formatterFile)) {
				configFromFile = readConfigFromWorkspaceMechanicFile(formatterFile);
			} else if (Settings.isXMLConfigurationFile(formatterFile)) {
				configFromFile = readConfigFromFormatterXmlFile(formatterFile, formatterProfile);
			} else if (Settings.isProjectSetting(formatterFile)) {
				configFromFile = readConfigFromProjectSettings(formatterFile);
			} else {
				configFromFile = new LinkedHashMap<>();
			}

			allConfig.putAll(ECLIPSE_JAVA_FORMATTER_DEFAULTS);
			allConfig.putAll(SOURCE_LEVEL_DEFAULTS);
			allConfig.putAll(configFromFile);
			allConfig.putAll(getSourceLevelOptions(sourceLevel));

			// https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=449262
			if (org.eclipse.jdt.core.JavaCore.DEFAULT_JAVA_FORMATTER.equals(allConfig.get("org.eclipse.jdt.core.javaFormatter"))) {
				//ignore default formatter as configured extension point
				allConfig.remove("org.eclipse.jdt.core.javaFormatter");
			}

			if (null != allConfig.get("org.eclipse.jdt.core.javaFormatter")) {
				throw new UnsupportedOperationException("The use of third-party Java code formatters is not supported by this plugin.\n"
						+ "See https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77\n"
						+ "Try to remove the entry 'org.eclipse.jdt.core.javaFormatter' from the configuration.");
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

	private Map<String, String> readConfigFromFormatterXmlFile(String formatterFile, String formatterProfile) throws ConfigReadException, ProfileNotFoundException, IOException, SAXException {
		List<Profile> profiles = new ConfigReader().read(FileUtil.normalizeFile(new File(formatterFile)));
		String name = formatterProfile;
		if (profiles.isEmpty()) {
			//no config found
			throw new ProfileNotFoundException("No profile found in " + formatterFile);
		}

		Profile profile = getProfileByName(profiles, name);
		if (null == profile) {
			throw new ProfileNotFoundException("Profile " + name + " not found in " + formatterFile);
		}

		return profile.getSettings();
	}

	private Map<String, String> readConfigFromWorkspaceMechanicFile(String formatterFile) throws IOException {
		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(formatterFile)) {
			properties.load(is);
		}

		return properties.keySet().stream().filter(key -> ((String) key).startsWith(WORKSPACE_MECHANIC_PREFIX))
				.collect(Collectors.toMap(key -> ((String) key).substring(WORKSPACE_MECHANIC_PREFIX.length()), key -> properties.getProperty((String) key)));
	}

	private Map<String, String> readConfigFromProjectSettings(final String formatterFile) throws IOException {
		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(formatterFile)) {
			properties.load(is);
		}

		return properties.keySet().stream().collect(Collectors.toMap(key -> (String) key, key -> properties.getProperty((String) key)));
	}
}
