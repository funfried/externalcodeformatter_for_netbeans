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

		String actual = instance.format("src/test/resources/jsformattersampleeclipse.xml", "eclipse-demo", text, null);
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

		String actual = instance.format("src/test/resources/jsmechanic-formatter.epf", null, text, null);
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

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, null);
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

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, Settings.getLineFeed("\\r", null));
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

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, Settings.getLineFeed("\\n", null));
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

		String actual = instance.format("src/test/resources/org.eclipse.wst.jsdt.core.prefs", null, text, Settings.getLineFeed("\\r\\n", null));
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNullProfile() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/jsformattersampleeclipse.xml", null, text, null);
		} catch (ProfileNotFoundException e) {
			Assert.assertEquals(true, e.getMessage().contains("Profile null not found"));
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

		String actual = instance.format(null, null, text, null);

		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testProfileNotFound() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/jsformattersampleeclipse.xml", "myProfile", text, null);
		} catch (ProfileNotFoundException e) {
			Assert.assertEquals(true, e.getMessage().contains("Profile myProfile not found"));
		}
	}

	@Test
	public void testNoProfileInConfig() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/defectjsformattersampleeclipse.xml", "myProfile", text, null);
		} catch (ConfigReadException e) {
			Assert.assertEquals(true, e.getMessage().contains("No <profiles> tag found in config file"));
		}
	}

	@Test
	public void testCannotLoadConfigFound() {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }";

		try {
			instance.format("src/test/resources/notexistent.xml", "myProfile", text, null);
		} catch (CannotLoadConfigurationException e) {
			Assert.assertEquals(true, e.getMessage().contains("src" + File.separator + "test" + File.separator + "resources" + File.separator + "notexistent.xml"));
		}
	}

	@Test
	public void testNullCode() {
		String actual = instance.format("src/test/resources/jsformattersampleeclipse.xml", "eclipse-demo", null, null);
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}
}
