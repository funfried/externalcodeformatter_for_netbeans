/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter.ui.options;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.openide.util.ChangeSupport;

/**
 * Abstract base implementation of {@link FormatterOptionsPanel}.
 *
 * @author bahlef
 */
public abstract class AbstractFormatterOptionsPanel extends JPanel implements FormatterOptionsPanel {
	/** {@link ChangeSupport} to notify about changed preference components. */
	protected final ChangeSupport changeSupport;

	/** Default constructor of {@link AbstractFormatterOptionsPanel}. */
	public AbstractFormatterOptionsPanel() {
		this.changeSupport = new ChangeSupport(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPanel getComponent() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChangeListener(ChangeListener listener) {
		changeSupport.addChangeListener(listener);
	}

	/**
	 * Fires a change event to all registered {@link ChangeListener}s.
	 */
	protected void fireChangedListener() {
		changeSupport.fireChange();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeChangeListener(ChangeListener listener) {
		changeSupport.removeChangeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean valid() {
		return true;
	}
}
