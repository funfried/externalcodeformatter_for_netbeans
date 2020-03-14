/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.xml.revelc;

/**
 * Utility class for revelc.net XML formatter specific settings.
 *
 * @author bahlef
 */
public class RevelcXmlFormatterSettings {
	/**
	 * Property key which defines the maximum line length for the revelc.net XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String MAX_LINE_LENGTH = "revelcXmlMaxLineLength";

	/**
	 * Property key which defines the line feed setting for the revelc.net XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String SPLIT_MULTI_ATTRIBUTES = "revelcXmlSplitMultiAttrs";

	/**
	 * Property key which defines to use tabs or spaces for the revelc.net XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String TAB_INSTEAD_OF_SPACES = "revelcXmlTabInsteadOfSpaces";

	/**
	 * Property key which defines amount of tabs for the revelc.net XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String TAB_WIDTH = "revelcXmlTabWidth";

	/**
	 * Property key which defines the handling of well formed validation for the revelc.net XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String WELL_FORMED_VALIDATION = "revelcXmlWellFormedValidation";

	/**
	 * Property key which defines whether to wrap long lines or not for the revelc.net XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String WRAP_LONG_LINES = "revelcXmlWrapLongLines";

	/**
	 * Property key which defines the line feed setting for the revelc.net XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String LINEFEED = "revelcXmlLinefeed";

	/**
	 * Private contructor because of static methods only.
	 */
	private RevelcXmlFormatterSettings() {
	}
}
