package org.netbeans.eclipse.formatter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.Icon;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.eclipse.formatter.options.EclipseFormatterPanel;
import org.netbeans.eclipse.formatter.xml.ConfigReadException;
import org.netbeans.eclipse.formatter.xml.ConfigReader;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;
import org.xml.sax.SAXException;

public final class EclipseFormatter implements PreferenceChangeListener {

    private File globalFile = null;
    private File localFile = null;
    private boolean isGlobalEclipseFormatterEnabled = false;
    private boolean isLocalEclipseFormatterEnabled = false;
    private boolean isLocalNetBeansFormatterEnabled = false;
    private static Preferences localPrefs;
    private static Preferences globalPrefs;
    private static Icon icon = EclipseFormatterUtilities.icon;

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        if (globalPrefs.getBoolean("globalEclipseFormatterDebug", false) == true) {
            NotificationDisplayer.getDefault().notify("'" + evt.getKey() + "' has changed", icon, "New value: " + evt.getNewValue(), null);
        }
        switch (evt.getKey()) {
            case "isLocalNetBeansFormatterEnabled":
                isLocalNetBeansFormatterEnabled = localPrefs.getBoolean(evt.getKey(), false);
                break;
            case "isLocalEclipseFormatterEnabled":
                isLocalEclipseFormatterEnabled = localPrefs.getBoolean(evt.getKey(), false);
                break;
            case "isGlobalEclipseFormatterEnabled":
                isGlobalEclipseFormatterEnabled = globalPrefs.getBoolean(evt.getKey(), false);
                break;
            case "localEclipseFormatterLocation":
                String PROJECT_FORMATTING_FILE = localPrefs.get(evt.getKey(), "");
                localFile = FileUtil.normalizeFile(new File(PROJECT_FORMATTING_FILE));
                break;
            case "globalEclipseFormatterLocation":
                String FORMATTING_FILE = globalPrefs.get(evt.getKey(), "");
                globalFile = FileUtil.normalizeFile(new File(FORMATTING_FILE));
                break;
        }
    }

    private Map getOptionsFromConfigFile() {
        globalPrefs = EclipseFormatterUtilities.getGlobalPrefs();
        globalPrefs.addPreferenceChangeListener(this);
        this.preferenceChange(new PreferenceChangeEvent(globalPrefs, "globalEclipseFormatterLocation", NbPreferences.forModule(EclipseFormatterPanel.class).get("globalEclipseFormatterLocation", "<no Eclipse formatter set>")));
        this.preferenceChange(new PreferenceChangeEvent(globalPrefs, "isGlobalEclipseFormatterEnabled", NbPreferences.forModule(EclipseFormatterPanel.class).get("isGlobalEclipseFormatterEnabled", "false")));
        FileObject currentFile = Utilities.actionsGlobalContext().lookup(FileObject.class);
        if (currentFile != null) {
            Project project = FileOwnerQuery.getOwner(currentFile);
            if (project != null) {
                if (globalPrefs.getBoolean("globalEclipseFormatterDebug", false) == true) {
                    NotificationDisplayer.getDefault().notify("Project", icon, ProjectUtils.getInformation(project).getDisplayName(), null);
                }
                localPrefs = ProjectUtils.getPreferences(project, IndentUtils.class, true);
                localPrefs.addPreferenceChangeListener(this);
                this.preferenceChange(new PreferenceChangeEvent(localPrefs, "isLocalNetBeansFormatterEnabled", NbPreferences.forModule(EclipseFormatterPanel.class).get("isLocalNetBeansFormatterEnabled", "false")));
                this.preferenceChange(new PreferenceChangeEvent(localPrefs, "isLocalEclipseFormatterEnabled", NbPreferences.forModule(EclipseFormatterPanel.class).get("isLocalEclipseFormatterEnabled", "false")));
            }
        }
        final ConfigReader configReader = new ConfigReader();
        Map options = new HashMap();
        if (isLocalNetBeansFormatterEnabled) {
            if (globalPrefs.getBoolean("globalEclipseFormatterDebug", false) == true) {
                NotificationDisplayer.getDefault().notify("Using the NetBeans formatter", icon, "", null);
            }
        } else if (isLocalEclipseFormatterEnabled) {
            if (localFile != null && localFile.exists()) {
                String message = localFile.getAbsolutePath();
                if (globalPrefs.getBoolean("globalEclipseFormatterDebug", false) == true) {
                    NotificationDisplayer.getDefault().notify("Using the Project-level Eclipse formatter", icon, message, null);
                }
                try {
                    options = configReader.read(localFile);
                } catch (IOException | SAXException | ConfigReadException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else if (isGlobalEclipseFormatterEnabled) {
            if (globalFile != null && globalFile.exists()) {
                String message = globalFile.getAbsolutePath();
                if (globalPrefs.getBoolean("globalEclipseFormatterDebug", false) == true) {
                    NotificationDisplayer.getDefault().notify("Using the Global Eclipse formatter", icon, message, null);
                }
                try {
                    options = configReader.read(globalFile);
                } catch (IOException | SAXException | ConfigReadException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else {
            if (globalPrefs.getBoolean("globalEclipseFormatterDebug", false) == true) {
                NotificationDisplayer.getDefault().notify("Using the NetBeans formatter", icon, "", null);
            }
        }
        return options;
    }

    private Map getFormattingOptions() {
        Map options = DefaultCodeFormatterConstants.getJavaConventionsSettings();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
//      For checking whether the Eclipse formatter works,
//      without needing an Eclipse formatter XML file:
//        options.put(
//		DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
//		DefaultCodeFormatterConstants.createAlignmentValue(
//		true,
//		DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
//		DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
        options.putAll(getOptionsFromConfigFile());
        return options;
    }

    private String format(final String code) throws MalformedTreeException, BadLocationException {
        final int opts = CodeFormatter.K_COMPILATION_UNIT + CodeFormatter.F_INCLUDE_COMMENTS;
        CodeFormatter formatter = ToolFactory.createCodeFormatter(getFormattingOptions());
        final TextEdit te = formatter.format(opts, code, 0, code.length(), 0, null);
        final IDocument dc = new Document(code);
        String formattedCode = code;
        if (te != null) {
            te.apply(dc);
            formattedCode = dc.get();
        }
        return formattedCode.toString();
    }

    public String forCode(final String code) {
        String result = code;
        try {
            if (code != null) {
                result = this.format(code);
            }
        } catch (MalformedTreeException ex) {
            System.out.println(ex);
            Logger.getLogger(EclipseFormatter.class.getName()).log(Level.SEVERE,
                    "code could not be formatted!", ex);
            System.out.println(ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(EclipseFormatter.class.getName()).log(Level.SEVERE,
                    "code could not be formatted!", ex);
        }
        return result;
    }
    
}
