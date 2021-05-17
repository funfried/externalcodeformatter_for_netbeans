/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

import org.netbeans.api.annotations.common.CheckForNull;

import com.manticore.jsqlformatter.JSQLFormatter;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Delegation class to the JSQLFormatter implementation.
 *
 * @author Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 */
public final class JSQLFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link JSQLFormatterWrapper}.
	 */
	JSQLFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted SQL code
	 * @param options an array of Formatting Options expressed as Key=Value pairs
	 *
	 * @return the formatted SQL code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, String... options) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		try {
			return JSQLFormatter.format(code, options);
		} catch (Exception ex) {
			throw new FormattingFailedException(ex);
		}
	}
}
