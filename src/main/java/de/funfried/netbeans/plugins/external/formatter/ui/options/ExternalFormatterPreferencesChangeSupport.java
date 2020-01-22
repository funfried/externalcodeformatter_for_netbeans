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

import javax.swing.event.ChangeListener;

import org.openide.util.ChangeSupport;
import org.openide.util.lookup.ServiceProvider;

/**
 * Holder of a {@link ChangeSupport} to fire and listen on property changes.
 *
 * @author bahlef
 */
@ServiceProvider(service = ExternalFormatterPreferencesChangeSupport.class)
public class ExternalFormatterPreferencesChangeSupport {
	private final ChangeSupport cs;

	public ExternalFormatterPreferencesChangeSupport() {
		cs = new ChangeSupport(this);
	}

	public void addChangeListener(ChangeListener listener) {
		cs.addChangeListener(listener);
	}

	public void fireChange() {
		cs.fireChange();
	}

	public void removeChangeListener(ChangeListener listener) {
		cs.removeChangeListener(listener);
	}
}
