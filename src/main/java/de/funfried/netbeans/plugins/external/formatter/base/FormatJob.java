/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter.base;

import javax.swing.text.BadLocationException;

/**
 *
 * @author bahlef
 */
public interface FormatJob {
	/**
	 * Executes this {@link FormatJob}.
	 *
	 * @throws BadLocationException if something goes wrong while applying the formatted code
	 */
	void format() throws BadLocationException;
}
