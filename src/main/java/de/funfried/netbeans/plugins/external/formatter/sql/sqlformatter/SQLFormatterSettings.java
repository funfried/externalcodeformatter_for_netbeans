/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.sqlformatter;

import com.github.vertical_blank.sqlformatter.languages.Dialect;

/**
 * Utility class for Vertical Blank SQL formatter specific settings.
 *
 * @author bahlef
 */
public class SQLFormatterSettings {
	/**
	 * Database to use.
	 */
	public static final String DIALECT = "sqlFormatterDialect";

	/**
	 * Default value for {@link #DIALECT}: {@link Dialect#StandardSql}.
	 */
	public static final String DIALECT_DEFAULT = Dialect.StandardSql.name();

	/**
	 * Convert query to uppercase, default is {@code false}.
	 */
	public static final String UPPERCASE = "sqlFormatterUppercase";

	/**
	 * Default value for {@link #UPPERCASE}: false.
	 */
	public static final boolean UPPERCASE_DEFAULT = false;

	/**
	 * Expand tabs to spaces, default is {@code true}.
	 */
	public static final String EXPAND_TABS_TO_SPACES = "sqlFormatterExpandTabsToSpaces";

	/**
	 * Default value for {@link #EXPAND_TABS_TO_SPACES}: true.
	 */
	public static final boolean EXPAND_TABS_TO_SPACES_DEFAULT = true;

	/**
	 * 2 by default.
	 */
	public static final String INDENT_SIZE = "sqlFormatterIndentSize";

	/**
	 * Default value for {@link #INDENT_SIZE}: 2.
	 */
	public static final int INDENT_SIZE_DEFAULT = 2;

	/**
	 * 1 by default.
	 */
	public static final String LINES_BETWEEN_QUERIES = "sqlFormatterLinesBetweenQueries";

	/**
	 * Default value for {@link #LINES_BETWEEN_QUERIES}: 1.
	 */
	public static final int LINES_BETWEEN_QUERIES_DEFAULT = 1;

	/**
	 * 50 by default.
	 */
	public static final String MAX_COLUMN_LENGTH = "sqlFormatterMaxColumnLength";

	/**
	 * Default value for {@link #MAX_COLUMN_LENGTH}: 50.
	 */
	public static final int MAX_COLUMN_LENGTH_DEFAULT = 50;

	/**
	 * Private contructor because of static methods only.
	 */
	private SQLFormatterSettings() {
	}
}
