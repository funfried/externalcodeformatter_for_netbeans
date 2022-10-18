/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.html.jsoup;

/**
 * Utility class for Jsoup HTML formatter specific settings.
 *
 * @author bahlef
 */
public class JsoupHtmlFormatterSettings {
	/**
	 * Property key which defines whether to use pretty print or not for the Jsoup HTML formatter.
	 *
	 * @since 1.15.3
	 */
	public static final String PRETTY_PRINT = "jsoupHtmlPrettyPrint";

	/**
	 * Property key which defines amount of spaces for indentation for the Jsoup HTML formatter.
	 *
	 * @since 1.15.3
	 */
	public static final String INDENT_SIZE = "jsoupHtmlIndentSize";

	/**
	 * Property key which defines whether to outline or not for the Jsoup HTML formatter.
	 *
	 * @since 1.15.3
	 */
	public static final String OUTLINE = "jsoupHtmlOutline";

	/**
	 * Property key which defines the line feed setting for the Jsoup HTML formatter.
	 *
	 * @since 1.15.3
	 */
	public static final String LINEFEED = "jsoupHtmlLinefeed";

	/**
	 * Private contructor because of static methods only.
	 */
	private JsoupHtmlFormatterSettings() {
	}
}
