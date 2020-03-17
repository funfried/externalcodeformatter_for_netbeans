/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.json.jackson;

/**
 * Utility class for Jackson Json formatter specific settings.
 *
 * @author bahlef
 */
public class JacksonJsonFormatterSettings {
	/**
	 * Property key which defines to use tabs or spaces for the Jackson Json formatter.
	 *
	 * @since 1.14
	 */
	public static final String EXPAND_TABS_TO_SPACES = "jacksonJsonExpandTabsToSpaces";

	/**
	 * Property key which defines amount of spaces per tabs for the Jackson Json formatter.
	 *
	 * @since 1.14
	 */
	public static final String SPACES_PER_TAB = "jacksonJsonSpacesPerTab";

	/**
	 * Property key which defines the amount of spaces for indentation for the Jackson Json formatter.
	 *
	 * @since 1.14
	 */
	public static final String INDENT_SIZE = "jacksonJsonIndentSize";

	/**
	 * Property key which defines whether or not adding a spaces between the key and value separator for the Jackson Json formatter.
	 *
	 * @since 1.14
	 */
	public static final String SPACE_BEFORE_SEPARATOR = "jacksonJsonSpaceBeforeSeparator";

	/**
	 * Property key which defines the line feed setting for the Jackson Json formatter.
	 *
	 * @since 1.14
	 */
	public static final String LINEFEED = "jacksonJsonLinefeed";

	/**
	 * Private contructor because of static methods only.
	 */
	private JacksonJsonFormatterSettings() {
	}
}
