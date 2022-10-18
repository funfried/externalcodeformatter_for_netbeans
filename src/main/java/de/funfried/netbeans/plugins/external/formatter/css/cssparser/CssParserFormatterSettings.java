/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.css.cssparser;

/**
 * Utility class for CssParser formatter specific settings.
 *
 * @author bahlef
 */
public class CssParserFormatterSettings {
	/**
	 * 4 by default.
	 *
	 * @since 1.15.3
	 */
	public static final String INDENT = "cssParserIndent";

	/**
	 * Default value for {@link #INDENT}: 4.
	 *
	 * @since 1.15.3
	 */
	public static final int INDENT_DEFAULT = 4;

	/**
	 * {@code true} by default.
	 *
	 * @since 1.15.3
	 */
	public static final String RGB_AS_HEX = "cssParserRgbAsHex";

	/**
	 * Default value for {@link #RGB_AS_HEX}: {@code true}.
	 *
	 * @since 1.15.3
	 */
	public static final boolean RGB_AS_HEX_DEFAULT = true;

	/**
	 * {@code false} by default.
	 *
	 * @since 1.15.3
	 */
	public static final String USE_SINGLE_QUOTES = "cssParserUseSingleQuotes";

	/**
	 * Default value for {@link #USE_SINGLE_QUOTES}: {@code false}.
	 *
	 * @since 1.15.3
	 */
	public static final boolean USE_SINGLE_QUOTES_DEFAULT = false;

	/**
	 * {@code false} by default.
	 *
	 * @since 1.15.3
	 */
	public static final String USE_SOURCE_STRING_VALUES = "cssParserUseSourceStringValues";

	/**
	 * Default value for {@link #USE_SOURCE_STRING_VALUES}: {@code false}.
	 *
	 * @since 1.15.3
	 */
	public static final boolean USE_SOURCE_STRING_VALUES_DEFAULT = false;

	/**
	 * Private contructor because of static methods only.
	 */
	private CssParserFormatterSettings() {
	}
}
