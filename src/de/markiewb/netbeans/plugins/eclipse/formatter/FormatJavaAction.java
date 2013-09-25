/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 */
package de.markiewb.netbeans.plugins.eclipse.formatter;

import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ECLIPSE_FORMATTER_ENABLED;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ECLIPSE_FORMATTER_LOCATION;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.SHOW_NOTIFICATIONS;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.getActivePreferences;
import java.util.prefs.Preferences;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import de.markiewb.netbeans.plugins.eclipse.formatter.customizer.ProjectSpecificSettingsPanel;
import de.markiewb.netbeans.plugins.eclipse.formatter.options.EclipseFormatterPanel;
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

        final boolean isEclipseFormatterEnabled = pref.getBoolean(ECLIPSE_FORMATTER_ENABLED, false);
        final boolean showNotifications = pref.getBoolean(SHOW_NOTIFICATIONS, false);

        if (!hasGuardedSections && isJava && isEclipseFormatterEnabled) {
            String formatterFile = pref.get(ECLIPSE_FORMATTER_LOCATION, null);
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

}
