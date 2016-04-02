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
package de.markiewb.netbeans.plugins.eclipse.formatter.strategies;

import de.markiewb.netbeans.plugins.eclipse.formatter.Utilities;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter.CannotLoadConfigurationException;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter.ProfileNotFoundException;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ECLIPSE_FORMATTER_ACTIVE_PROFILE;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ECLIPSE_FORMATTER_ENABLED;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ECLIPSE_FORMATTER_LOCATION;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.LINEFEED;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.PRESERVE_BREAKPOINTS;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.PROJECT_PREF_FILE;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.SHOW_NOTIFICATIONS;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.SOURCELEVEL;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.USE_PROJECT_PREFS;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.getActivePreferences;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.getLineFeed;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatterStrategy;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.netbeans.NetBeansFormatterStrategy;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;

/**
 *
 * @author markiewb
 */
public class FormatterStrategyDispatcher {

    EclipseFormatterStrategy eclipseStrategy = new EclipseFormatterStrategy();
    NetBeansFormatterStrategy netbeansStrategy = new NetBeansFormatterStrategy();

    public void formatWithNetBeans(final boolean showNotifications, final boolean hasGuardedSections, final boolean isEclipseFormatterEnabled, final boolean isJava, ParameterObject po) {
        if (showNotifications) {
            String detail = getNotificationMessageForNetBeans(hasGuardedSections, isEclipseFormatterEnabled, isJava);

            NotificationDisplayer.getDefault().notify("Format using NetBeans formatter", Utilities.iconNetBeans, detail, null);
        }
        StatusDisplayer.getDefault().setStatusText("Format using NetBeans formatter");
        boolean preserveBreakpoints = false;
        netbeansStrategy.format(null, preserveBreakpoints, po);
    }

    public void format(ParameterObject po) {
        final StyledDocument styledDoc = po.styledDoc;
        GuardedSectionManager guards = GuardedSectionManager.getInstance(styledDoc);
        final boolean hasGuardedSections = guards != null;
        final boolean isJava = Utilities.isJava(styledDoc);
        Preferences pref = getActivePreferences(styledDoc);

        final boolean isEclipseFormatterEnabled = pref.getBoolean(ECLIPSE_FORMATTER_ENABLED, false);
        final boolean showNotifications = pref.getBoolean(SHOW_NOTIFICATIONS, false);
        final boolean preserveBreakpoints = pref.getBoolean(PRESERVE_BREAKPOINTS, true);
        final boolean useProjectPrefs = pref.getBoolean(USE_PROJECT_PREFS, true);
        final String lineFeed = pref.get(LINEFEED, "");
        final String sourceLevel = pref.get(SOURCELEVEL, "");
        if (!hasGuardedSections && isJava && isEclipseFormatterEnabled) {
            String formatterProfile = pref.get(ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
            String formatterFile = getFormatterFileFromProjectConfiguration(useProjectPrefs, styledDoc);
            if (null == formatterFile) {
                formatterFile = pref.get(ECLIPSE_FORMATTER_LOCATION, null);
            }

            if (!new File(formatterFile).exists()) {
                //fallback to NB
                formatWithNetBeans(showNotifications, hasGuardedSections, isEclipseFormatterEnabled, isJava, po);
                return;
            }

            //format with configured linefeed
            final EclipseFormatter formatter = new EclipseFormatter(formatterFile, formatterProfile, lineFeed, sourceLevel);

            try {
                //save with configured linefeed
                if (null != lineFeed) {
                    styledDoc.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, getLineFeed(lineFeed));
                    styledDoc.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, getLineFeed(lineFeed));
                }
                eclipseStrategy.format(formatter, preserveBreakpoints, po);
            } catch (ProfileNotFoundException e) {
                NotifyDescriptor notify = new NotifyDescriptor.Message(String.format("<html>Profile '%s' not found in <tt>%s</tt><br><br>Please configure a valid one in the project properties OR at Tools|Options|Java|Eclipse Formatter!", formatterProfile, formatterFile), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(notify);
            } catch (CannotLoadConfigurationException e) {
                NotifyDescriptor notify = new NotifyDescriptor.Message(String.format("<html>Could not find configuration file %s.<br>Make sure the file exists and it can be read.", formatterFile), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(notify);
                return;
            }

            String msg = getNotificationMessageForEclipseFormatterConfigurationFileType(formatterFile, formatterProfile);

            if (showNotifications) {
                NotificationDisplayer.getDefault().notify("Format using Eclipse formatter 4.4", Utilities.iconEclipse, msg, null);
            }
            StatusDisplayer.getDefault().setStatusText("Format using Eclipse formatter 4.4: " + msg);

        } else {
            formatWithNetBeans(showNotifications, hasGuardedSections, isEclipseFormatterEnabled, isJava, po);
        }
    }

    public String getFormatterFileFromProjectConfiguration(final boolean useProjectPrefs, final StyledDocument styledDoc) {
        //use ${projectdir}/.settings/org.eclipse.jdt.core.prefs, if activated in options
        if (useProjectPrefs) {
            FileObject fileForDocument = NbEditorUtilities.getFileObject(styledDoc);
            if (null != fileForDocument) {

                Project project = FileOwnerQuery.getOwner(fileForDocument);
                if (null != project) {
                    FileObject projectDirectory = project.getProjectDirectory();
                    FileObject preferenceFile = projectDirectory.getFileObject(".settings/" + PROJECT_PREF_FILE);
                    if (null != preferenceFile) {
                        return preferenceFile.getPath();
                    }
                }
            }
        }
        return null;
    }

    public String getNotificationMessageForEclipseFormatterConfigurationFileType(String formatterFile, String formatterProfile) {
        String msg = "";
        if (de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.isWorkspaceMechanicFile(formatterFile)) {
            //Workspace mechanic file
            msg = String.format("Using %s", formatterFile);
        } else if (de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.isXMLConfigurationFile(formatterFile)) {
            //XML file
            msg = String.format("Using profile '%s' from %s", formatterProfile, formatterFile);
        } else if (de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.isProjectSetting(formatterFile)) {
            //org.eclipse.jdt.core.prefs
            msg = String.format("Using %s", formatterFile);
        }
        return msg;
    }

    public String getNotificationMessageForNetBeans(final boolean hasGuardedSections, final boolean isEclipseFormatterEnabled, final boolean isJava) {
        String detail = "";
        if (hasGuardedSections && isEclipseFormatterEnabled) {
            detail += "Because file contains guarded sections. ";
        }
        if (!isJava) {
            detail += "Because file isn't a Java file. ";
        }
        return detail;
    }

}
