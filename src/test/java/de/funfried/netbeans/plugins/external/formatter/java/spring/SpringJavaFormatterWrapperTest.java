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
package de.funfried.netbeans.plugins.external.formatter.java.spring;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class SpringJavaFormatterWrapperTest {
	private static SpringJavaFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new SpringJavaFormatterWrapper();
	}

	@Test
	public void testFormatUsingXML() {
		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "\n"
				+ "	A, B, C\n"
				+ "\n"
				+ "}";

		String actual = instance.format(text, null, null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingXML_formatterOnOff() {
		final String text = "package foo; public class NewEmptyJUnitTest {\n" +
				"\n" +
				"// @formatter:off\n" +
				"public void doSomething(String arg) {\n" +
				"	System.out.println(\"Hello World\");\n" +
				"}\n" +
				"// @formatter:on\n" +
				"\n" +
				"public boolean doSomethingElse(Object obj) { return false; } }";
		final String expected = "package foo;\n" +
				"\n" +
				"public class NewEmptyJUnitTest {\n" +
				"\n" +
				"// @formatter:off\n" +
				"public void doSomething(String arg) {\n" +
				"	System.out.println(\"Hello World\");\n" +
				"}\n" +
				"// @formatter:on\n" +
				"\n" +
				"	public boolean doSomethingElse(Object obj) {\n" +
				"		return false;\n" +
				"	}\n" +
				"\n" +
				"}";

		String actual = instance.format(text, null, null);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingXML_regionAll() {
		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "\n"
				+ "	A, B, C\n"
				+ "\n"
				+ "}";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(0, text.length() - 1));

		String actual = instance.format(text, null, regions);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingXML_regions() throws Exception {
		final String text = "package foo; public class Bar {\n" +
				"    @SuppressWarnings(\"unchecked\")\n" +
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); } public boolean doSomethingElse(Object obj) { return false; } }";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				"    @SuppressWarnings(\"unchecked\")\n" +
				// START -- this should not be formatted
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); }\n" +
				// END -- this should not be formatted
				"\n" +
				"	public boolean doSomethingElse(Object obj) {\n" +
				"		return false;\n" +
				"	}\n" +
				"\n" +
				"}";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(0, 30));
		regions.add(Pair.of(171, 233));

		String actual = instance.format(text, null, regions);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testFormatUsingLinefeed_CR() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\r"
				+ "\r"
				+ "public enum NewEmptyJUnitTest {\r"
				+ "\r"
				+ "	A, B, C\r"
				+ "\r"
				+ "}";

		String actual = instance.format(text, Settings.getLineFeed("\\r", null), null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_LF() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\n"
				+ "\n"
				+ "public enum NewEmptyJUnitTest {\n"
				+ "\n"
				+ "	A, B, C\n"
				+ "\n"
				+ "}";

		String actual = instance.format(text, Settings.getLineFeed("\\n", null), null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatUsingLinefeed_CRLF() {
		final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
		final String expected = "package foo;\r\n"
				+ "\r\n"
				+ "public enum NewEmptyJUnitTest {\r\n"
				+ "\r\n"
				+ "	A, B, C\r\n"
				+ "\r\n"
				+ "}";

		String actual = instance.format(text, Settings.getLineFeed("\\r\\n", null), null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNullCode() {
		String actual = instance.format(null, null, null);
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}

	@Test
	public void testIllegalChangedElementsCode() {
		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(1200, 1230));

		try {
			instance.format(text, null, regions);

			Assert.assertFalse("Should have thrown an IllegalArgumentException", true);
		} catch (IllegalArgumentException ex) {
			Assert.assertTrue(true);
		}
	}
}
