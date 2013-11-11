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

import de.markiewb.netbeans.plugins.eclipse.formatter.options.EclipseFormatterPanel;
import javax.swing.text.StyledDocument;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.NbPreferences;

/**
 *
 * @author markiewb
 */
public class Preferences {

    public static final String SHOW_NOTIFICATIONS = "showNotifications";
    public static final String ECLIPSE_FORMATTER_ENABLED = "eclipseFormatterEnabled";
    public static final String ECLIPSE_FORMATTER_ACTIVE_PROFILE = "eclipseFormatterActiveProfile";
    public static final String ECLIPSE_FORMATTER_LOCATION = "eclipseFormatterLocation";
    public static final String ENABLE_SAVEACTION = "enableFormatAsSaveAction";
    public static final String USE_PROJECT_SETTINGS = "useProjectSettings";

    public static java.util.prefs.Preferences getActivePreferences(final StyledDocument styledDoc) {
        java.util.prefs.Preferences globalPreferences = NbPreferences.forModule(EclipseFormatterPanel.class);
        Project project = FileOwnerQuery.getOwner(NbEditorUtilities.getDataObject(styledDoc).getPrimaryFile());
        if (null != project) {
//            NotificationDisplayer.getDefault().notify("Project", null, "" + project, null);
            java.util.prefs.Preferences projectPreferences = ProjectUtils.getPreferences(project, EclipseFormatterPanel.class, true);
            if (projectPreferences.getBoolean(USE_PROJECT_SETTINGS, false)) {
                return projectPreferences;
            } else {
                return globalPreferences;
            }
        } else {
            return globalPreferences;
        }
    }
}
