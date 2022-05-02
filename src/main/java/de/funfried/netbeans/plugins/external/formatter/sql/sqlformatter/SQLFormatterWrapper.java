/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.sqlformatter;

import org.netbeans.api.annotations.common.CheckForNull;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import com.github.vertical_blank.sqlformatter.languages.Dialect;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Delegation class to the Vertical Blank SQL formatter implementation.
 *
 * @author bahlef
 */
public final class SQLFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link SQLFormatterWrapper}.
	 */
	SQLFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted SQL code
	 * @param dialect the {@link Dialect} to use
	 * @param formatConfig Formatting options as a {@link FormatConfig} object
	 *
	 * @return the formatted SQL code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, Dialect dialect, FormatConfig formatConfig) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		try {
			return SqlFormatter.of(dialect).format(code, formatConfig);
		} catch (Exception ex) {
			throw new FormattingFailedException(ex);
		}
	}
}
