/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

/**
 * Utility class for Google Java formatter specific settings.
 *
 * @author bahlef
 */
public class JSQLFormatterSettings {
	/**
	 * Property key which defines which code style should be used for the JSQLFormatter.
	 *
	 * @since 1.13
	 */
	public static final String SQL_FORMATTER_CODE_STYLE = "sqlormatterCodeStyle";

	/**
	 * Private constructor because of static methods only.
	 */
	private JSQLFormatterSettings() {
	}
}
