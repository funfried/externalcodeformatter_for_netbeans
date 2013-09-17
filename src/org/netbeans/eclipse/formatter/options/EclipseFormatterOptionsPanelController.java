package org.netbeans.eclipse.formatter.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

@OptionsPanelController.SubRegistration(
        location = "Java",
        displayName = "#AdvancedOption_DisplayName_EclipseFormatter",
        keywords = "#AdvancedOption_Keywords_EclipseFormatter",
        keywordsCategory = "Java/EclipseFormatter")
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_EclipseFormatter=Eclipse Formatter", "AdvancedOption_Keywords_EclipseFormatter=format Eclipse"})
public final class EclipseFormatterOptionsPanelController extends OptionsPanelController {

    private EclipseFormatterPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
//        Preferences pref=NbPreferences.forModule(EclipseFormatterPanel.class);
//        Preferences projectPrefs = ProjectUtils.getPreferences(project, EclipseFormatterPanel.class, true);
        panel.load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        panel.store();
        changed = false;
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return panel.valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public EclipseFormatterPanel getComponent(Lookup masterLookup) {
        final Project project = masterLookup.lookup(Project.class);
        
        Preferences preferences;
        if (null==project){
            preferences = NbPreferences.forModule(EclipseFormatterPanel.class);
        }else{
            preferences = ProjectUtils.getPreferences(project, EclipseFormatterPanel.class, true);
        }
        return getPanel(preferences);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public EclipseFormatterPanel getPanel(Preferences preferences) {
        if (panel == null) {
            panel = new EclipseFormatterPanel(this, preferences);
        }
        return panel;
    }

    public JTextField getLocationField() {
        return panel.getFormatterLocField();
    }
    
    public JRadioButton getEnablement() {
        return panel.getEnabledCheckbox();
    }
    
    public JRadioButton getNetBeans() {
        return panel.getNetBeansCheckbox();
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
