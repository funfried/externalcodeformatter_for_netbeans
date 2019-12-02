/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml.ConfigReadException;
import org.junit.Assert;
import org.junit.Test;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import org.junit.BeforeClass;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class EclipseFormatterTest {
	private static EclipseFormatter instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new EclipseFormatter();
	}

	@Test
	public void testFormatUsingXML() {
		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "			       A,\n"
				+ "				   B,\n"
				+ "				   C\n"
				+ "}";

		String actual = instance.format("src/test/resources/formattersampleeclipse.xml", "eclipse-demo", text, null, null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingEPF() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "			       A,\n"
				+ "				   B,\n"
				+ "				   C\n"
				+ "}";

		String actual = instance.format("src/test/resources/mechanic-formatter.epf", null, text, null, null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingProjectSettings() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "			       A,\n"
				+ "				   B,\n"
				+ "				   C\n"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.jdt.core.prefs", null, text, null, null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	/**
	 * https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77
	 */
	@Test
	public void testFormatUsingProjectSettings_ExplicitDefaultFormatter() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "			       A,\n"
				+ "				   B,\n"
				+ "				   C\n"
				+ "}";

		String actual = instance.format("src/test/resources/defaultformatter_org.eclipse.jdt.core.prefs", null, text, null, null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	/**
	 * https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77
	 */
	@Test
	public void testFormatUsingProjectSettings_Explicit3rdPartyFormatter_Failure() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";

		try {
			instance.format("src/test/resources/3rdPartyFormatter_org.eclipse.jdt.core.prefs", null, text, null, null, null);
		} catch (Exception e) {
			Assert.assertEquals(true, e.getMessage().contains("The use of third-party Java code formatters is not supported by this plugin."));
		}
	}

	@Test
	public void testFormatUsingLinefeed_CR() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\r"
				+ "\r"
				+ "public enum NewEmptyJUnitTest {\r"
				+ "			       A,\r"
				+ "				   B,\r"
				+ "				   C\r"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.jdt.core.prefs", null, text, Settings.getLineFeed("\\r", System.getProperty("line.separator")), null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_LF() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "			       A,\n"
				+ "				   B,\n"
				+ "				   C\n"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.jdt.core.prefs", null, text, Settings.getLineFeed("\\n", System.getProperty("line.separator")), null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_CRLF() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\r\n"
				+ "\r\n"
				+ "public enum NewEmptyJUnitTest {\r\n"
				+ "			       A,\r\n"
				+ "				   B,\r\n"
				+ "				   C\r\n"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.jdt.core.prefs", null, text, Settings.getLineFeed("\\r\\n", System.getProperty("line.separator")), null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatSourceLevel_1dot3() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = null;

		String actual = instance.format("src/test/resources/org.eclipse.jdt.core.prefs", null, text, Settings.getLineFeed("\\n", System.getProperty("line.separator")), "1.3", null);
		Assert.assertEquals("Invalid source code for 1.3 - enum is not a keyword", expected, actual);
	}

	@Test
	public void testFormatSourceLevel_1dot4() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = null;

		String actual = instance.format("src/test/resources/org.eclipse.jdt.core.prefs", null, text, Settings.getLineFeed("\\n", System.getProperty("line.separator")), "1.4", null);
		Assert.assertEquals("Invalid source code for 1.4 - enum is not a keyword", expected, actual);
	}

	@Test
	public void testFormatSourceLevel_1dot5() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "			       A,\n"
				+ "				   B,\n"
				+ "				   C\n"
				+ "}";

		String actual = instance.format("src/test/resources/org.eclipse.jdt.core.prefs", null, text, Settings.getLineFeed("\\n", System.getProperty("line.separator")), "1.5", null);
		Assert.assertEquals("Invalid source code for 1.4 - enum is not a keyword", expected, actual);
	}

	@Test
	public void testNullProfile() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";

		try {
			instance.format("src/test/resources/formattersampleeclipse.xml", null, text, null, null, null);
		} catch (ProfileNotFoundException e) {
			Assert.assertEquals(true, e.getMessage().contains("Profile null not found"));
		}
	}

	@Test
	public void testNullFormatterConfig() {
		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"    A, B, C\n" +
				"}";

		String actual = instance.format(null, null, text, null, null, null);

		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testProfileNotFound() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";

		try {
			instance.format("src/test/resources/formattersampleeclipse.xml", "myProfile", text, null, null, null);
		} catch (ProfileNotFoundException e) {
			Assert.assertEquals(true, e.getMessage().contains("Profile myProfile not found"));
		}
	}

	@Test
	public void testNoProfileInConfig() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";

		try {
			instance.format("src/test/resources/defectformattersampleeclipse.xml", "myProfile", text, null, null, null);
		} catch (ConfigReadException e) {
			Assert.assertEquals(true, e.getMessage().contains("No profiles found in config file"));
		}
	}

	@Test
	public void testCannotLoadConfigFound() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";

		try {
			instance.format("src/test/resources/notexistent.xml", "myProfile", text, null, null, null);
		} catch (CannotLoadConfigurationException e) {
			Assert.assertEquals(true, e.getMessage().contains("src/test/resources/notexistent.xml"));
		}
	}
}
