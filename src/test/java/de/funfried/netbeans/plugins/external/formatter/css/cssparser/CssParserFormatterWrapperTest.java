/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * bahlef
 */
package de.funfried.netbeans.plugins.external.formatter.css.cssparser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class CssParserFormatterWrapperTest {
	private static CssParserFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new CssParserFormatterWrapper();
	}

	@Test
	public void testFormatUsingDefaults() {
		final String text = ".foo { width: 100%; height: 200px; color: rgb(255, 0, 255); background: url(\"../test.png\"); } span.bar { width: 20pt; height: 80%; display: flow; }\n";
		final String expected = ".foo {\n"
				+ "    width: 100%;\n"
				+ "    height: 200px;\n"
				+ "    color: #ff00ff;\n"
				+ "    background: url(../test.png)\n"
				+ "}\n"
				+ "span.bar {\n"
				+ "    width: 20pt;\n"
				+ "    height: 80%;\n"
				+ "    display: flow\n"
				+ "}";

		String actual = instance.format(text, CssParserFormatterSettings.INDENT_DEFAULT, CssParserFormatterSettings.RGB_AS_HEX_DEFAULT, CssParserFormatterSettings.USE_SINGLE_QUOTES_DEFAULT,
				CssParserFormatterSettings.USE_SOURCE_STRING_VALUES_DEFAULT);

		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingNoRgbAsHex() {
		final String text = ".foo { width: 100%; height: 200px; color: rgb(255, 0, 255); background: url(\"../test.png\"); } span.bar { width: 20pt; height: 80%; display: flow; }\n";
		final String expected = ".foo {\n"
				+ "    width: 100%;\n"
				+ "    height: 200px;\n"
				+ "    color: rgb(255, 0, 255);\n"
				+ "    background: url(../test.png)\n"
				+ "}\n"
				+ "span.bar {\n"
				+ "    width: 20pt;\n"
				+ "    height: 80%;\n"
				+ "    display: flow\n"
				+ "}";

		String actual = instance.format(text, CssParserFormatterSettings.INDENT_DEFAULT, false, CssParserFormatterSettings.USE_SINGLE_QUOTES_DEFAULT,
				CssParserFormatterSettings.USE_SOURCE_STRING_VALUES_DEFAULT);

		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingSingleQuotes() {
		final String text = ".foo { width: 100%; height: 200px; color: rgb(255, 0, 255); background: url(\"../test.png\"); } span.bar { width: 20pt; height: 80%; display: flow; }\n";
		final String expected = ".foo {\n"
				+ "    width: 100%;\n"
				+ "    height: 200px;\n"
				+ "    color: #ff00ff;\n"
				+ "    background: url(../test.png)\n"
				+ "}\n"
				+ "span.bar {\n"
				+ "    width: 20pt;\n"
				+ "    height: 80%;\n"
				+ "    display: flow\n"
				+ "}";

		String actual = instance.format(text, CssParserFormatterSettings.INDENT_DEFAULT, CssParserFormatterSettings.RGB_AS_HEX_DEFAULT, true, CssParserFormatterSettings.USE_SOURCE_STRING_VALUES_DEFAULT);

		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}
}
