/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.dbeaver;

import java.util.Properties;

import org.netbeans.api.annotations.common.CheckForNull;

import com.diffplug.spotless.sql.dbeaver.DBeaverSQLFormatterConfiguration;
import com.diffplug.spotless.sql.dbeaver.SQLTokenizedFormatter;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Delegation class to the DBeaver SQL formatter implementation.
 *
 * @author bahlef
 */
public final class DBeaverFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link DBeaverFormatterWrapper}.
	 */
	DBeaverFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted SQL code
	 * @param properties Formatting Options as {@link Properties}
	 *
	 * @return the formatted SQL code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, Properties properties) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		try {
			DBeaverSQLFormatterConfiguration configuration = new DBeaverSQLFormatterConfiguration(properties);
			SQLTokenizedFormatter sqlTokenizedFormatter = new SQLTokenizedFormatter(configuration);
			return sqlTokenizedFormatter.format(code);
		} catch (Exception ex) {
			throw new FormattingFailedException(ex);
		}
	}
}
