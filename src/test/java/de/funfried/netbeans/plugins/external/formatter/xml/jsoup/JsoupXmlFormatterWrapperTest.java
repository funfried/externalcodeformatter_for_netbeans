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

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class JsoupXmlFormatterWrapperTest {
	private static JsoupXmlFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new JsoupXmlFormatterWrapper();
	}

	@Test
	public void testFormat() {
		final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><foo bar=\"value\"><elem>val</elem></foo><otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">otherValue</otherTag></root>";
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<root>\n"
				+ " <foo bar=\"value\">\n"
				+ "  <elem>\n"
				+ "   val\n"
				+ "  </elem>\n"
				+ " </foo>\n"
				+ " <otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">\n"
				+ "  otherValue\n"
				+ " </otherTag>\n"
				+ "</root>";

		String actual = instance.format(text, null, new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingLinefeed_CR() {
		final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><foo bar=\"value\"><elem>val</elem></foo><otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">otherValue</otherTag></root>";
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r"
				+ "<root>\r"
				+ " <foo bar=\"value\">\r"
				+ "  <elem>\r"
				+ "   val\r"
				+ "  </elem>\r"
				+ " </foo>\r"
				+ " <otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">\r"
				+ "  otherValue\r"
				+ " </otherTag>\r"
				+ "</root>";

		String actual = instance.format(text, Settings.getLineFeed("\\r", null), new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_LF() {
		final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><foo bar=\"value\"><elem>val</elem></foo><otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">otherValue</otherTag></root>";
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<root>\n"
				+ " <foo bar=\"value\">\n"
				+ "  <elem>\n"
				+ "   val\n"
				+ "  </elem>\n"
				+ " </foo>\n"
				+ " <otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">\n"
				+ "  otherValue\n"
				+ " </otherTag>\n"
				+ "</root>";

		String actual = instance.format(text, Settings.getLineFeed("\\n", null), new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_CRLF() {
		final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><foo bar=\"value\"><elem>val</elem></foo><otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">otherValue</otherTag></root>";
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<root>\r\n"
				+ " <foo bar=\"value\">\r\n"
				+ "  <elem>\r\n"
				+ "   val\r\n"
				+ "  </elem>\r\n"
				+ " </foo>\r\n"
				+ " <otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">\r\n"
				+ "  otherValue\r\n"
				+ " </otherTag>\r\n"
				+ "</root>";

		String actual = instance.format(text, Settings.getLineFeed("\\r\\n", null), new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNullFormatterConfig() {
		final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><foo bar=\"value\"><elem>val</elem></foo><otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">otherValue</otherTag></root>";
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<root>\n"
				+ " <foo bar=\"value\">\n"
				+ "  <elem>\n"
				+ "   val\n"
				+ "  </elem>\n"
				+ " </foo>\n"
				+ " <otherTag attrib=\"attribValue\" attrib2=\"attribValue2\">\n"
				+ "  otherValue\n"
				+ " </otherTag>\n"
				+ "</root>";

		String actual = instance.format(text, null, null);

		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testNullCode() {
		String actual = instance.format(null, null, new Document.OutputSettings());
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}
}
