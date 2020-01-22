/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui.options;

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

/**
 * {@link OptionsPanelController} implementation for the {@link ExternalFormatterPanel}.
 *
 * @author markiewb
 */
@OptionsPanelController.SubRegistration(id = "de.funfried.netbeans.plugins.external.formatter.ui.options", location = "Java", displayName = "#AdvancedOption_DisplayName_ExternalFormatter", keywords = "#AdvancedOption_Keywords_ExternalFormatter", keywordsCategory = "Java/ExternalFormatter")
@org.openide.util.NbBundle.Messages({ "AdvancedOption_DisplayName_ExternalFormatter=External Formatter", "AdvancedOption_Keywords_ExternalFormatter=External Formatter" })
public final class ExternalFormatterOptionsPanelController extends OptionsPanelController implements ChangeListener {
	private ExternalFormatterPanel panel;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private boolean changed;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update() {
		createOrGetPanel().load();
		changed = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applyChanges() {
		createOrGetPanel().store();
		changed = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancel() {
		// need not do anything special, if no changes have been persisted yet
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return createOrGetPanel().valid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HelpCtx getHelpCtx() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExternalFormatterPanel getComponent(Lookup masterLookup) {
		return createOrGetPanel();
	}

	/**
	 * Returns the cached {@link ExternalFormatterPanel} or creates a new one and caches it for the next call.
	 *
	 * @return the cached {@link ExternalFormatterPanel} or creates a new one and caches it for the next call
	 */
	private ExternalFormatterPanel createOrGetPanel() {
		if (null == panel) {
			Preferences globalPreferences = NbPreferences.forModule(ExternalFormatterPanel.class);
			panel = new ExternalFormatterPanel(globalPreferences, false);
			panel.addChangeListener(WeakListeners.change(this, panel));
		}

		return panel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	/**
	 * {@inheritDoc}
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
