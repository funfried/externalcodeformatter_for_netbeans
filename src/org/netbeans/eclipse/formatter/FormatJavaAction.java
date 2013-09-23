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

        if (!hasGuardedSections && isJava && isEclipseFormatterEnabled) {
            String formatterFile = pref.get(EclipseFormatterPanel.ECLIPSE_FORMATTER_LOCATION, null);
            final EclipseFormatter formatter = EclipseFormatterUtilities.getEclipseFormatter(formatterFile);
            NotificationDisplayer.getDefault().notify("Eclipse formatter", EclipseFormatterUtilities.icon, "Formatted using " + formatter + formatterFile, null);
            u.reFormatWithEclipse(styledDoc, formatter);

        } else {
            NotificationDisplayer.getDefault().notify("NetBeans formatter", EclipseFormatterUtilities.icon, "Formatting using NB", null);
            u.reformatWithNetBeans(styledDoc);
        }
    }

    private Preferences getActivePreferences(final StyledDocument styledDoc) {
        Preferences globalPreferences = NbPreferences.forModule(EclipseFormatterPanel.class);
        Project project = FileOwnerQuery.getOwner(NbEditorUtilities.getDataObject(styledDoc).getPrimaryFile());
        if (null != project) {
            NotificationDisplayer.getDefault().notify("Project", EclipseFormatterUtilities.icon, "" + project, null);
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
