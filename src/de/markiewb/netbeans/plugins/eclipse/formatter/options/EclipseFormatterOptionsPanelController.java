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
package de.markiewb.netbeans.plugins.eclipse.formatter.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;

@OptionsPanelController.SubRegistration(
        id = "de.markiewb.netbeans.plugins.eclipse.formatter.options",
        location = "Java",
        displayName = "#AdvancedOption_DisplayName_EclipseFormatter",
        keywords = "#AdvancedOption_Keywords_EclipseFormatter",
        keywordsCategory = "Java/EclipseFormatter")
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_EclipseFormatter=Eclipse Formatter", "AdvancedOption_Keywords_EclipseFormatter=format Eclipse"})
public final class EclipseFormatterOptionsPanelController extends OptionsPanelController implements ChangeListener {

    private EclipseFormatterPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        createOrGetPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        createOrGetPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return createOrGetPanel().valid();
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
        return createOrGetPanel();
    }

    private EclipseFormatterPanel createOrGetPanel() {
        if (null == panel) {
            Preferences globalPreferences = NbPreferences.forModule(EclipseFormatterPanel.class);
            panel = new EclipseFormatterPanel(globalPreferences, false);
            panel.addChangeListener(WeakListeners.change (this, panel));
        }
        return panel;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }


    /**
     * Something in the panel has changed, so inform the listeners of this controller too.
     * @param e 
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
