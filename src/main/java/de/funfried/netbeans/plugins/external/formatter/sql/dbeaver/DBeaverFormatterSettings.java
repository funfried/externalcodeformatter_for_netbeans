/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.dbeaver;

/**
 * Utility class for DBeaver SQL formatter specific settings.
 *
 * @author bahlef
 */
public class DBeaverFormatterSettings {
	/**
	 * UPPER, LOWER or ORIGINAL.
	 */
	public static final String KEYWORD_CASE = "sql.formatter.keyword.case";

	/**
	 * Default value for {@link #KEYWORD_CASE}: UPPER.
	 */
	public static final String KEYWORD_CASE_DEFAULT = "UPPER";

	/**
	 * ';' by default.
	 */
	public static final String STATEMENT_DELIMITER = "sql.formatter.statement.delimiter";

	/**
	 * Default value for {@link #STATEMENT_DELIMITER}: ;.
	 */
	public static final String STATEMENT_DELIMITER_DEFAULT = ";";

	/**
	 * space or tab.
	 */
	public static final String INDENT_TYPE = "sql.formatter.indent.type";

	/**
	 * Default value for {@link #INDENT_TYPE}: space.
	 */
	public static final String INDENT_TYPE_DEFAULT = "space";

	/**
	 * 4 by default.
	 */
	public static final String INDENT_SIZE = "sql.formatter.indent.size";

	/**
	 * Default value for {@link #INDENT_SIZE}: 4.
	 */
	public static final int INDENT_SIZE_DEFAULT = 4;

	/**
	 * Private contructor because of static methods only.
	 */
	private DBeaverFormatterSettings() {
	}
}
