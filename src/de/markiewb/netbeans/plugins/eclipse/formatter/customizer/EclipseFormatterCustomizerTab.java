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
package de.markiewb.netbeans.plugins.eclipse.formatter.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import de.markiewb.netbeans.plugins.eclipse.formatter.options.EclipseFormatterPanel;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

public class EclipseFormatterCustomizerTab implements ProjectCustomizer.CompositeCategoryProvider {

    private final String name;
    private Listener listener;

    private EclipseFormatterCustomizerTab(String name) {
        this.name = name;
    }

    
    @StaticResource
    private static final String ICON = "de/markiewb/netbeans/plugins/eclipse/formatter/eclipse.gif";
    
    
    @Override
    public Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create(name, name, ImageUtilities.loadImage(ICON, false));
    }

    @Override
    public JComponent createComponent(final Category category, final Lookup lkp) {
        Preferences projectPreferences = ProjectUtils.getPreferences(lkp.lookup(Project.class), EclipseFormatterPanel.class, true);
        final EclipseFormatterPanel configPanel = new EclipseFormatterPanel(projectPreferences, true);
        final ProjectSpecificSettingsPanel projectSpecificSettingsPanel = new ProjectSpecificSettingsPanel(configPanel, projectPreferences);
        configPanel.load();
        projectSpecificSettingsPanel.load();

        category.setStoreListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configPanel.store();
                projectSpecificSettingsPanel.store();
            }
        });

        listener = new Listener(category, projectSpecificSettingsPanel);
        configPanel.addChangeListener(WeakListeners.change(listener, configPanel));
        projectSpecificSettingsPanel.addChangeListener(WeakListeners.change(listener, projectSpecificSettingsPanel));
        return projectSpecificSettingsPanel;
    }

    @NbBundle.Messages({"LBL_Config=Eclipse Formatting"})
    @Registrations({
        @Registration(category = "Formatting", projectType = "org-netbeans-modules-java-j2seproject", position = 1000),
        @Registration(category = "Formatting", projectType = "org-netbeans-modules-web-project", position = 1000),
        @Registration(category = "Formatting", projectType = "org-netbeans-modules-maven", position = 1000),
        @Registration(category = "Formatting", projectType = "org-netbeans-modules-apisupport-project", position = 1000)
    })
    public static EclipseFormatterCustomizerTab createMyDemoConfigurationTab() {
        return new EclipseFormatterCustomizerTab(Bundle.LBL_Config());
    }

    private class Listener implements ChangeListener {

        private final Category category;
        private final ProjectSpecificSettingsPanel projectSpecificPanel;

        private Listener(Category category, ProjectSpecificSettingsPanel projectSpecificPanel) {
            this.category = category;
            this.projectSpecificPanel = projectSpecificPanel;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            category.setValid(projectSpecificPanel.holdsValidConfig());
        }
    }
}
