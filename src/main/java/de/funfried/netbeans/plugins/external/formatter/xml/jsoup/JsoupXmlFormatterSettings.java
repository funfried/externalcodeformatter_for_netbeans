/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.xml.jsoup;

/**
 * Utility class for Jsoup XML formatter specific settings.
 *
 * @author bahlef
 */
public class JsoupXmlFormatterSettings {
	/**
	 * Property key which defines whether to use pretty print or not for the Jsoup XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String PRETTY_PRINT = "jsoupXmlPrettyPrint";

	/**
	 * Property key which defines amount of spaces for indentation for the Jsoup XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String INDENT_SIZE = "jsoupXmlIndentSize";

	/**
	 * Property key which defines whether to outline or not for the Jsoup XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String OUTLINE = "jsoupXmlOutline";

	/**
	 * Property key which defines the line feed setting for the Jsoup XML formatter.
	 *
	 * @since 1.14
	 */
	public static final String LINEFEED = "jsoupXmlLinefeed";

	/**
	 * Private contructor because of static methods only.
	 */
	private JsoupXmlFormatterSettings() {
	}
}
