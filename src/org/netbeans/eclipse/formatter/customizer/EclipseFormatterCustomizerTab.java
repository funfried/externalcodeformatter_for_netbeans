package org.netbeans.eclipse.formatter.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.eclipse.formatter.options.EclipseFormatterOptionsPanelController;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class EclipseFormatterCustomizerTab implements ProjectCustomizer.CompositeCategoryProvider {

    private final String name;

    private EclipseFormatterCustomizerTab(String name) {
        this.name = name;
    }

    @Override
    public Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create(name, name, null);
    }

    @Override
    public JComponent createComponent(final Category category, final Lookup lkp) {
        final Project project = lkp.lookup(Project.class);
        final EclipseFormatterOptionsPanelController efopc = new EclipseFormatterOptionsPanelController();
        JComponent component = efopc.getComponent(lkp);
        efopc.update();
        category.setOkButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences projectPrefs = ProjectUtils.getPreferences(project, IndentUtils.class, true);
                if (!efopc.getEnablement().isSelected()) {
                    String location = efopc.getLocationField().getText();
                    projectPrefs.put("localEclipseFormatterLocation", location);
                    projectPrefs.putBoolean("isLocalEclipseFormatterEnabled", false);
                } else if (efopc.getEnablement().isSelected()) {
                    String location = efopc.getLocationField().getText();
                    projectPrefs.put("localEclipseFormatterLocation", location);
                    projectPrefs.putBoolean("isLocalEclipseFormatterEnabled", true);
                }
                if (efopc.getNetBeans().isSelected()) {
                    projectPrefs.putBoolean("isLocalNetBeansFormatterEnabled", true);
                } else {
                    projectPrefs.putBoolean("isLocalNetBeansFormatterEnabled", false);
                }
            }
        });
        efopc.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!efopc.isValid()) {
                    category.setValid(false);
                }
                else if (efopc.isValid() && !efopc.getEnablement().isSelected()) {
                    category.setValid(true);
                } else if (efopc.isValid() && efopc.getEnablement().isSelected()) {
                    category.setValid(true);
                }
            }
        });
        return component;
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
}