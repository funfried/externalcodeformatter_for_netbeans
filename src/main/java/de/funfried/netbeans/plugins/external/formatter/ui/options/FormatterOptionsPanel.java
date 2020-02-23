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

import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

/**
 *
 * @author bahlef
 */
public interface FormatterOptionsPanel {
	/**
	 * Returns the {@link JPanel} component for this {@link FormatterOptionsPanel}.
	 *
	 * @return the {@link JPanel} component for this {@link FormatterOptionsPanel}
	 */
	JPanel getComponent();

	/**
	 * Adds a {@link ChangeListener} to this {@link FormatterOptionsPanel} which
	 * will be informed if a user makes changes to the shown components.
	 *
	 * @param listener the {@link ChangeListener} to add
	 *
	 * @see #removeChangeListener(javax.swing.event.ChangeListener)
	 */
	void addChangeListener(ChangeListener listener);

	/**
	 * Loads the current settings from the given {@link Preferences} and sets those
	 * to the components of this {@link FormatterOptionsPanel}.
	 *
	 * @param preferences the {@link Preferences} to load from
	 */
	void load(Preferences preferences);

	/**
	 * Removes a {@link ChangeListener} from this {@link FormatterOptionsPanel}.
	 *
	 * @param listener the {@link ChangeListener} to remove
	 *
	 * @see #addChangeListener(javax.swing.event.ChangeListener)
	 */
	void removeChangeListener(ChangeListener listener);

	/**
	 * If {@code active} is set to {@code true}, this {@link FormatterOptionsPanel}
	 * will be set as the currently active, otherwise it will be inactive. This is
	 * usually used to disable and enable all of the components.
	 *
	 * @param active {@code true} to set this {@link FormatterOptionsPanel} as the
	 *               currently active one, otherwise {@code false}
	 */
	void setActive(boolean active);

	/**
	 * Stores all the currently set values of all components to the given
	 * {@link Preferences}.
	 *
	 * @param preferences the {@link Preferences} where to store to
	 */
	void store(Preferences preferences);

	/**
	 * Returns {@code true} if the settings that are currently made are valid,
	 * otherwise {@code false}.
	 *
	 * @return {@code true} if the settings that are currently made are valid,
	 *         otherwise {@code false}
	 */
	boolean valid();
}
