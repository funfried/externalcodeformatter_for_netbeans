/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 *    Saad Mufti <saad.mufti@teamaol.com> 
 */
package de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse;

import de.markiewb.netbeans.plugins.eclipse.formatter.Pair;
import de.markiewb.netbeans.plugins.eclipse.formatter.v44.options.Preferences;
import static de.markiewb.netbeans.plugins.eclipse.formatter.v44.options.Preferences.getLineFeed;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReadException;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReader;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.Profile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.logging.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.TextEdit;
import org.openide.filesystems.FileUtil;
import org.xml.sax.SAXException;

public final class EclipseFormatter {

    private static final Logger LOG = Logger.getLogger(EclipseFormatter.class.getName());

    private final String formatterFile;
    private final String formatterProfile;
    private final String lineFeedSetting;
    private final String sourceLevel;

    public EclipseFormatter(String formatterFile, String formatterProfile, String lineFeed, String sourceLevel) {
        this.formatterFile = formatterFile;
        this.formatterProfile = formatterProfile;
        this.lineFeedSetting = lineFeed;
        this.sourceLevel = sourceLevel;
    }

    public String forCode(final String code, int startOffset, int endOffset, SortedSet<Pair> changedElements) {
        String result = null;
        if (code != null) {
            result = this.format(code, startOffset, endOffset, changedElements);
        }
        return result;
    }

    // returns null if format resulted in no change
    private String format(final String code, int startOffset, int endOffset, SortedSet<Pair> changedElements) {
        final int opts
                = CodeFormatter.K_COMPILATION_UNIT + CodeFormatter.F_INCLUDE_COMMENTS /*+ CodeFormatter.K_CLASS_BODY_DECLARATIONS + CodeFormatter.K_STATEMENTS*/;
        Map<String, String> allConfig = readConfig();

        CodeFormatter formatter = ToolFactory.createCodeFormatter(allConfig);
        //see http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fcore%2Fformatter%2FCodeFormatter.html&anchor=format(int,

        String linefeed = getLineFeed(lineFeedSetting);
        final TextEdit te;
        // org.eclipse.jface.text.Region
        List<IRegion> regions = new ArrayList<>();
        if (null != changedElements && !changedElements.isEmpty()) {
            for (Pair e : changedElements) {
                final int length = e.getSecond() - e.getFirst();
                regions.add(new org.eclipse.jface.text.Region(e.getFirst(), length));
            }
            LOG.finest("regions = " + regions);
            IRegion[] toArray = regions.toArray(new IRegion[regions.size()]);
            LOG.finest("use regions " + regions);
            te = formatter.format(opts, code, toArray, 0, linefeed);
        } else {
            te = formatter.format(opts, code, startOffset, endOffset - startOffset, 0, linefeed);
        }

        final IDocument dc = new Document(code);
        String formattedCode = null;
        if ((te != null) && (te.getChildrenSize() > 0)) {
            try {
                te.apply(dc);
            } catch (Exception ex) {
                LOG.warning("Code could not be formatted!" + ex);
                return null;
            }
            formattedCode = dc.get();
        }
        return formattedCode;
    }

    private Map<String, String> getSourceLevelOptions() {
        Map<String, String> options = new HashMap<>();
        if (null != sourceLevel && !"".equals(sourceLevel)) {
            String level = sourceLevel;
            options.put(JavaCore.COMPILER_COMPLIANCE, level);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
            options.put(JavaCore.COMPILER_SOURCE, level);
        }
        return options;
    }

    private Map<String, String> getSourceLevelDefaults() {
        String level = JavaCore.VERSION_1_6;
        Map<String, String> options = new HashMap<>();
        options.put(JavaCore.COMPILER_COMPLIANCE, level);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, level);
        options.put(JavaCore.COMPILER_SOURCE, level);
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

    private Map<String, String> readConfig() throws ProfileNotFoundException {
        Map<String, String> allConfig = new HashMap<>();
        try {
            final File file = new File(formatterFile);
            Map<String, String> configFromFile = new LinkedHashMap<>();
            if (Preferences.isWorkspaceMechanicFile(formatterFile)) {
                configFromFile.putAll(readConfigFromWorkspaceMechanicFile(file));
            } else if (Preferences.isXMLConfigurationFile(formatterFile)) {
                configFromFile.putAll(readConfigFromFormatterXmlFile(file));
            } else if (Preferences.isProjectSetting(formatterFile)) {
                configFromFile.putAll(readConfigFromProjectSettings(file));
            }

            allConfig.putAll(DefaultCodeFormatterConstants.getJavaConventionsSettings());
            allConfig.putAll(getSourceLevelDefaults());
            allConfig.putAll(configFromFile);
            allConfig.putAll(getSourceLevelOptions());
        } catch (Exception ex) {
            LOG.warning("Could not load configuration: " + formatterFile + ex);

            throw new CannotLoadConfigurationException(ex);
        }
        return allConfig;
    }

    private Map<String, String> readConfigFromFormatterXmlFile(final File file) throws ConfigReadException, ProfileNotFoundException, IOException, SAXException {
        Map<String, String> configFromFile;
        List<Profile> profiles = new ConfigReader().read(FileUtil.normalizeFile(file));
        String name = formatterProfile;
        if (profiles.isEmpty()) {
            //no config found
            throw new ProfileNotFoundException("No profiles found in " + formatterFile);
        }
        Profile profile = getProfileByName(profiles, name);
        if (null == profile) {
            throw new ProfileNotFoundException("profile " + name + " not found in " + formatterFile);
        }
        configFromFile = profile.getSettings();
        return configFromFile;
    }

    private Map<String, String> readConfigFromWorkspaceMechanicFile(final File file) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(file)) {
            properties.load(is);
        }
        final String prefix = "/instance/org.eclipse.jdt.core/";
        for (Object object : properties.keySet()) {
            String key = (String) object;
            if (key.startsWith(prefix)) {
                String value = properties.getProperty(key);
                result.put(key.substring(prefix.length()), value);
            }
        }
        return result;
    }

    private Map<String, String> readConfigFromProjectSettings(final File file) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(file)) {
            properties.load(is);
        }
        for (Object object : properties.keySet()) {
            String key = (String) object;
            result.put(key, properties.getProperty(key));
        }
        return result;
    }

    public class CannotLoadConfigurationException extends RuntimeException {

        public CannotLoadConfigurationException(Exception ex) {
            super(ex);
        }
    }

    public class ProfileNotFoundException extends RuntimeException {

        public ProfileNotFoundException(String message) {
            super(message);
        }
    }

}
