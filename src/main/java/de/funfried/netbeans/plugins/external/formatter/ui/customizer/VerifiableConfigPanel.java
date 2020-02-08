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

/**
 * Interface to define a verifiable configuration panel.
 *
 * @author markiewb
 */
public interface VerifiableConfigPanel {
	/**
	 * Returns {@code true} if and only if the configuration is valid, otherwise
	 * {@code false}.
	 *
	 * @return {@code true} if and only if the configuration is valid, otherwise
	 *         {@code false}
	 */
	boolean valid();

	/**
	 * Loads the configuration and sets the values to the UI components.
	 */
	void load();

	/**
	 * Stores the configuration from the current state of UI components.
	 */
	void store();
}
