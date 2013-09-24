/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.eclipse.formatter;

import java.util.prefs.Preferences;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.eclipse.formatter.customizer.ProjectSpecificSettingsPanel;
import org.netbeans.eclipse.formatter.options.EclipseFormatterPanel;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbPreferences;

/**
 *
 * @author markiewb
 */
public class FormatJavaAction {

    void format(final StyledDocument styledDoc) {
        GuardedSectionManager guards = GuardedSectionManager.getInstance(styledDoc);
        EclipseFormatterUtilities u = new EclipseFormatterUtilities();
        final boolean hasGuardedSections = guards != null;
        final boolean isJava = EclipseFormatterUtilities.isJava(styledDoc);
        Preferences pref = getActivePreferences(styledDoc);

        final boolean isEclipseFormatterEnabled = pref.getBoolean(EclipseFormatterPanel.ECLIPSE_FORMATTER_ENABLED, false);
        final boolean showNotifications = pref.getBoolean(EclipseFormatterPanel.SHOW_NOTIFICATIONS, false);

        if (!hasGuardedSections && isJava && isEclipseFormatterEnabled) {
            String formatterFile = pref.get(EclipseFormatterPanel.ECLIPSE_FORMATTER_LOCATION, null);
            final EclipseFormatter formatter = EclipseFormatterUtilities.getEclipseFormatter(formatterFile);

            if (showNotifications) {
                NotificationDisplayer.getDefault().notify("Format using Eclipse formatter", EclipseFormatterUtilities.iconEclipse, formatterFile, null);
            }
            StatusDisplayer.getDefault().setStatusText("Format using Eclipse formatter");
            u.reFormatWithEclipse(styledDoc, formatter);

        } else {

            if (showNotifications) {
                String detail="";
                if (hasGuardedSections && isEclipseFormatterEnabled){
                    detail+="Because file contains guarded sections. ";
                }
                if (!isJava) {
                    detail+="Because file isn't a Java file. ";
                    
                }
                
                NotificationDisplayer.getDefault().notify("Format using NetBeans formatter", EclipseFormatterUtilities.iconNetBeans, detail, null);
            }
            StatusDisplayer.getDefault().setStatusText("Format using NetBeans formatter");
            u.reformatWithNetBeans(styledDoc);
        }
    }

    private Preferences getActivePreferences(final StyledDocument styledDoc) {
        Preferences globalPreferences = NbPreferences.forModule(EclipseFormatterPanel.class);
        Project project = FileOwnerQuery.getOwner(NbEditorUtilities.getDataObject(styledDoc).getPrimaryFile());
        if (null != project) {
//            NotificationDisplayer.getDefault().notify("Project", null, "" + project, null);
            Preferences projectPreferences = ProjectUtils.getPreferences(project, EclipseFormatterPanel.class, true);
            if (projectPreferences.getBoolean(ProjectSpecificSettingsPanel.USE_PROJECT_SETTINGS, false)) {
                return projectPreferences;
            } else {
                return globalPreferences;
            }
        } else {
            return globalPreferences;
        }
    }

}
