/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.javascript.eclipse;

import java.io.File;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class EclipseJavascriptFormatterWrapperTest {
	private static EclipseJavascriptFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new EclipseJavascriptFormatterWrapper();
	}

	@Test
	public void testFormatUsingXML() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\n"
				+ "    return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []);\n"
				+ "}";

		String actual = instance.format("src/test/resources/jsformattersampleeclipse.xml", "eclipse-demo", text, null, null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingXML_regionAll() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\n"
				+ "    return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []);\n"
				+ "}";

		String actual = instance.format("src/test/resources/jsformattersampleeclipse.xml", "eclipse-demo", text, null, Pair.of(0, text.length() - 1));
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingXML_region() throws Exception {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) { return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []); }";

		String actual = instance.format("src/test/resources/jsformattersampleeclipse.xml", "eclipse-demo", text, null, Pair.of(27, 47));
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingEPF() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\n"
				+ "    return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []);\n"
				+ "}";

		String actual = instance.format("src/test/resources/jsmechanic-formatter.epf", null, text, null, null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingProjectSettings() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\n"
				+ "    return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []);\n"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, null, null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingLinefeed_CR() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\r"
				+ "    return Str('', {\r"
				+ "        '' : bar\r"
				+ "    }, []);\r"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, Settings.getLineFeed("\\r", null), null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_LF() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\n"
				+ "    return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []);\n"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, Settings.getLineFeed("\\n", null), null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_CRLF() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\r\n"
				+ "    return Str('', {\r\n"
				+ "        '' : bar\r\n"
				+ "    }, []);\r\n"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, Settings.getLineFeed("\\r\\n", null), null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNullProfile() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/jsformattersampleeclipse.xml", null, text, null, null);
		} catch (ProfileNotFoundException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("Profile null not found"));
		}
	}

	@Test
	public void testNullFormatterConfig() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";
		final String expected = "function foo(bar) {\n"
				+ "    return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []);\n"
				+ "}";

		String actual = instance.format(null, null, text, null, null);

		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testProfileNotFound() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/jsformattersampleeclipse.xml", "myProfile", text, null, null);
		} catch (ProfileNotFoundException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("Profile myProfile not found"));
		}
	}

	@Test
	public void testNoProfileInConfig() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/defectjsformattersampleeclipse.xml", "myProfile", text, null, null);
		} catch (ConfigReadException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("No <profiles> tag found in config file"));
		}
	}

	@Test
	public void testCannotLoadConfigFound() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/notexistent.xml", "myProfile", text, null, null);
		} catch (CannotLoadConfigurationException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("src" + File.separator + "test" + File.separator + "resources" + File.separator + "notexistent.xml"));
		}
	}

	@Test
	public void testNullCode() {
		String actual = instance.format("src/test/resources/jsformattersampleeclipse.xml", "eclipse-demo", null, null, null);
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}
}
