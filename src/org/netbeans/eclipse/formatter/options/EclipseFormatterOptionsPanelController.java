package org.netbeans.eclipse.formatter.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.netbeans.api.project.Project;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

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
        getPanelForGlobalConfig().load();
        changed = false;
    }

    private EclipseFormatterPanel getPanelForGlobalConfig() {
        return getPanel(null);
    }

    @Override
    public void applyChanges() {
        getPanelForGlobalConfig().store();
        changed = false;
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanelForGlobalConfig().valid();
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
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel(masterLookup);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public EclipseFormatterPanel getPanel(Lookup lkp) {
        if (panel == null) {
            if (lkp != null) {
                panel = new EclipseFormatterPanel(this, lkp.lookup(Project.class));
            } else {
                panel = new EclipseFormatterPanel(this, null);
            }
        }
        return panel;
    }

    public JTextField getLocationField() {
        return getPanelForGlobalConfig().getFormatterLocField();
    }
    
    public JRadioButton getEnablement() {
        return getPanelForGlobalConfig().getEnabledCheckbox();
    }
    
    public JRadioButton getNetBeans() {
        return getPanelForGlobalConfig().getNetBeansCheckbox();
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
