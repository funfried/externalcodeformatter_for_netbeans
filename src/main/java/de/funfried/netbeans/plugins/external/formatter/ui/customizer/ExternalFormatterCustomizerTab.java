/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * bahlef
 */
package de.funfried.netbeans.plugins.external.formatter.ui.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.ExternalFormatterPanel;

/**
 * {@link ProjectCustomizer.CompositeCategoryProvider} implementation for project
 * specific external formatting properties tab provider.
 *
 * @author markiewb
 * @author bahlef
 */
@NbBundle.Messages({ "LBL_Config=External Formatting" })
@ProjectCustomizer.CompositeCategoryProvider.Registrations({
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-j2ee-clientproject", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-j2ee-ejbjarproject", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-java-j2seproject", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-java-j2semodule", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-j2ee-earproject", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-ant-freeform", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-web-project", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-maven", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-gradle", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-apisupport-project", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-web-clientproject", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-php-project", position = 1000),
		@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "Formatting", projectType = "org-netbeans-modules-mobility-project", position = 1000)
})
public class ExternalFormatterCustomizerTab implements ProjectCustomizer.CompositeCategoryProvider {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Category createCategory(Lookup lkp) {
		return ProjectCustomizer.Category.create("external-format", NbBundle.getMessage(ExternalFormatterCustomizerTab.class, "LBL_Config"),
				ImageUtilities.loadImage(Icons.EXTERNAL_FORMATTER_ICON_PATH, false));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JComponent createComponent(final Category category, final Lookup lkp) {
		Project project = lkp.lookup(Project.class);
		Preferences projectPreferences = ProjectUtils.getPreferences(project, ExternalFormatterPanel.class, true);
		final ExternalFormatterPanel configPanel = new ExternalFormatterPanel(projectPreferences, project);
		final ProjectSpecificSettingsPanel projectSpecificSettingsPanel = new ProjectSpecificSettingsPanel(configPanel, projectPreferences);
		configPanel.load();
		projectSpecificSettingsPanel.load();

		category.setStoreListener(new ActionListener() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				projectSpecificSettingsPanel.store();
				configPanel.store();
			}
		});

		ValidationListener listener = new ValidationListener(category, projectSpecificSettingsPanel);
		configPanel.addChangeListener(WeakListeners.change(listener, configPanel));
		projectSpecificSettingsPanel.addChangeListener(WeakListeners.change(listener, projectSpecificSettingsPanel));

		return projectSpecificSettingsPanel;
	}

	/**
	 * {@link ChangeListener} to keep track on validation changes.
	 */
	private static class ValidationListener implements ChangeListener {
		/** The {@link Category} where to update the validation. */
		private final Category category;

		/** The {@link ProjectSpecificSettingsPanel} where to check the validation. */
		private final ProjectSpecificSettingsPanel projectSpecificPanel;

		/**
		 * Creates a new instance of the {@link ValidationListener}.
		 *
		 * @param category the {@link Category} where to update the validation
		 * @param projectSpecificPanel the {@link ProjectSpecificSettingsPanel} where to check the validation
		 */
		private ValidationListener(Category category, ProjectSpecificSettingsPanel projectSpecificPanel) {
			this.category = category;
			this.projectSpecificPanel = projectSpecificPanel;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			category.setValid(projectSpecificPanel.valid());
		}
	}
}
