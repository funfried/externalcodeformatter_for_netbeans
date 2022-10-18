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

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class JsoupHtmlFormatterWrapperTest {
	private static JsoupHtmlFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new JsoupHtmlFormatterWrapper();
	}

	@Test
	public void testFormat() {
		final String text = "<!DOCTYPE html><html><head><title>foobar</title></head><body width=\"100%\"><div><p>some text</p><img src=\"../img.png\" alt=\"Some image\"></div></body></html>\n";
		final String expected = "<!doctype html>\n"
				+ "<html>\n"
				+ " <head>\n"
				+ "  <title>foobar</title>\n"
				+ " </head>\n"
				+ " <body width=\"100%\">\n"
				+ "  <div>\n"
				+ "   <p>some text</p><img src=\"../img.png\" alt=\"Some image\">\n"
				+ "  </div>\n"
				+ " </body>\n"
				+ "</html>";

		String actual = instance.format(text, null, new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingLinefeed_CR() {
		final String text = "<!DOCTYPE html><html><head><title>foobar</title></head><body width=\"100%\"><div><p>some text</p><img src=\"../img.png\" alt=\"Some image\"></div></body></html>\n";
		final String expected = "<!doctype html>\r"
				+ "<html>\r"
				+ " <head>\r"
				+ "  <title>foobar</title>\r"
				+ " </head>\r"
				+ " <body width=\"100%\">\r"
				+ "  <div>\r"
				+ "   <p>some text</p><img src=\"../img.png\" alt=\"Some image\">\r"
				+ "  </div>\r"
				+ " </body>\r"
				+ "</html>";

		String actual = instance.format(text, Settings.getLineFeed("\\r", null), new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_LF() {
		final String text = "<!DOCTYPE html><html><head><title>foobar</title></head><body width=\"100%\"><div><p>some text</p><img src=\"../img.png\" alt=\"Some image\"></div></body></html>\n";
		final String expected = "<!doctype html>\n"
				+ "<html>\n"
				+ " <head>\n"
				+ "  <title>foobar</title>\n"
				+ " </head>\n"
				+ " <body width=\"100%\">\n"
				+ "  <div>\n"
				+ "   <p>some text</p><img src=\"../img.png\" alt=\"Some image\">\n"
				+ "  </div>\n"
				+ " </body>\n"
				+ "</html>";

		String actual = instance.format(text, Settings.getLineFeed("\\n", null), new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_CRLF() {
		final String text = "<!DOCTYPE html><html><head><title>foobar</title></head><body width=\"100%\"><div><p>some text</p><img src=\"../img.png\" alt=\"Some image\"></div></body></html>\n";
		final String expected = "<!doctype html>\r\n"
				+ "<html>\r\n"
				+ " <head>\r\n"
				+ "  <title>foobar</title>\r\n"
				+ " </head>\r\n"
				+ " <body width=\"100%\">\r\n"
				+ "  <div>\r\n"
				+ "   <p>some text</p><img src=\"../img.png\" alt=\"Some image\">\r\n"
				+ "  </div>\r\n"
				+ " </body>\r\n"
				+ "</html>";

		String actual = instance.format(text, Settings.getLineFeed("\\r\\n", null), new Document.OutputSettings());
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNullFormatterConfig() {
		final String text = "<!DOCTYPE html><html><head><title>foobar</title></head><body width=\"100%\"><div><p>some text</p><img src=\"../img.png\" alt=\"Some image\"></div></body></html>\n";
		final String expected = "<!doctype html>\n"
				+ "<html>\n"
				+ " <head>\n"
				+ "  <title>foobar</title>\n"
				+ " </head>\n"
				+ " <body width=\"100%\">\n"
				+ "  <div>\n"
				+ "   <p>some text</p><img src=\"../img.png\" alt=\"Some image\">\n"
				+ "  </div>\n"
				+ " </body>\n"
				+ "</html>";

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
