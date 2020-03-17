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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class JacksonJsonFormatterWrapperTest {
	private static JacksonJsonFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new JacksonJsonFormatterWrapper();
	}

	@Test
	public void testFormat() {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}";
		final String expected = "{\n" +
				"  \"foo\": \"bar\",\n" +
				"  \"array\": [\n" +
				"    \"value1\",\n" +
				"    \"value2\",\n" +
				"    \"value3\"\n" +
				"  ],\n" +
				"  \"object\": {\n" +
				"    \"string\": \"value\",\n" +
				"    \"boolean\": false,\n" +
				"    \"number\": 2\n" +
				"  }\n" +
				"}";

		String actual = instance.format(text, null, new JacksonJsonFormatterWrapper.Options());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormat_tabsInsteadOfSpaces() {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}";
		final String expected = "{\n" +
				"		\"foo\" : \"bar\",\n" +
				"		\"array\" : [\n" +
				"				\"value1\",\n" +
				"				\"value2\",\n" +
				"				\"value3\"\n" +
				"		],\n" +
				"		\"object\" : {\n" +
				"				\"string\" : \"value\",\n" +
				"				\"boolean\" : false,\n" +
				"				\"number\" : 2\n" +
				"		}\n" +
				"}";

		JacksonJsonFormatterWrapper.Options options = new JacksonJsonFormatterWrapper.Options();
		options.setSpaceBeforeSeparator(true);
		options.setExpandTabsToSpaces(false);
		options.setIndentSize(4);

		String actual = instance.format(text, null, options);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingLinefeed_CR() {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}";
		final String expected = "{\r" +
				"  \"foo\": \"bar\",\r" +
				"  \"array\": [\r" +
				"    \"value1\",\r" +
				"    \"value2\",\r" +
				"    \"value3\"\r" +
				"  ],\r" +
				"  \"object\": {\r" +
				"    \"string\": \"value\",\r" +
				"    \"boolean\": false,\r" +
				"    \"number\": 2\r" +
				"  }\r" +
				"}";

		String actual = instance.format(text, Settings.getLineFeed("\\r", null), new JacksonJsonFormatterWrapper.Options());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_LF() {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}";
		final String expected = "{\n" +
				"  \"foo\": \"bar\",\n" +
				"  \"array\": [\n" +
				"    \"value1\",\n" +
				"    \"value2\",\n" +
				"    \"value3\"\n" +
				"  ],\n" +
				"  \"object\": {\n" +
				"    \"string\": \"value\",\n" +
				"    \"boolean\": false,\n" +
				"    \"number\": 2\n" +
				"  }\n" +
				"}";

		String actual = instance.format(text, Settings.getLineFeed("\\n", null), new JacksonJsonFormatterWrapper.Options());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_CRLF() {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}";
		final String expected = "{\r\n" +
				"  \"foo\": \"bar\",\r\n" +
				"  \"array\": [\r\n" +
				"    \"value1\",\r\n" +
				"    \"value2\",\r\n" +
				"    \"value3\"\r\n" +
				"  ],\r\n" +
				"  \"object\": {\r\n" +
				"    \"string\": \"value\",\r\n" +
				"    \"boolean\": false,\r\n" +
				"    \"number\": 2\r\n" +
				"  }\r\n" +
				"}";

		String actual = instance.format(text, Settings.getLineFeed("\\r\\n", null), new JacksonJsonFormatterWrapper.Options());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNullFormatterConfig() {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}";
		final String expected = "{\n" +
				"  \"foo\": \"bar\",\n" +
				"  \"array\": [\n" +
				"    \"value1\",\n" +
				"    \"value2\",\n" +
				"    \"value3\"\n" +
				"  ],\n" +
				"  \"object\": {\n" +
				"    \"string\": \"value\",\n" +
				"    \"boolean\": false,\n" +
				"    \"number\": 2\n" +
				"  }\n" +
				"}";

		String actual = instance.format(text, null, null);

		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testNullCode() {
		String actual = instance.format(null, null, new JacksonJsonFormatterWrapper.Options());
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}
}
