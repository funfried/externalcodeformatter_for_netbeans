/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.google;

import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.googlejavaformat.java.JavaFormatterOptions;

/**
 *
 * @author bahlef
 */
public class GoogleJavaFormatterWrapperTest {
	private static GoogleJavaFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		Locale.setDefault(Locale.ENGLISH);

		instance = new GoogleJavaFormatterWrapper();
	}

	@Test
	public void testFormatEnumUsingGoogleStyle() {
		final String text = "package foo;public enum Bar {A,B,C}";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum Bar {\n" +
				"  A,\n" +
				"  B,\n" +
				"  C\n" +
				"}\n"
				+ "";

		String actual = instance.format(text, JavaFormatterOptions.Style.GOOGLE, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatEnumUsingAOSPStyle() {
		final String text = "package foo;public enum Bar {A,B,C}";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum Bar {\n" +
				"    A,\n" +
				"    B,\n" +
				"    C\n" +
				"}\n"
				+ "";

		String actual = instance.format(text, JavaFormatterOptions.Style.AOSP, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatEnumWithDifferentLineEndingsUsingGoogleStyle() {
		final String text = "package foo;public enum Bar {\r"
				+ "A,\r"
				+ "B,\r"
				+ "C\r"
				+ "}";
		final String expected = "package foo;\r" +
				"\r" +
				"public enum Bar {\r" +
				"  A,\r" +
				"  B,\r" +
				"  C\r" +
				"}\r"
				+ "";

		String actual = instance.format(text, JavaFormatterOptions.Style.GOOGLE, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatClassUsingGoogleStyle() {
		final String text = "package foo; public class Bar {public void doSomething(String arg) { System.out.println(\"Hello World\"); }}";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				"  public void doSomething(String arg) {\n" +
				"    System.out.println(\"Hello World\");\n" +
				"  }\n" +
				"}\n" +
				"";

		String actual = instance.format(text, JavaFormatterOptions.Style.GOOGLE, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Currently failing: It seems there is a bug with character ranges in the Google Java Code Formatter
	 * https://github.com/google/google-java-format/issues/433
	 */
	@Test
	public void testPartlyFormatClassUsingGoogleStyle() {
		final String text = "package foo; public class Bar {\n" +
				"    @SuppressWarnings(\"unchecked\")\n" +
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); } public boolean doSomethingElse(Object obj) { return false; } }";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				"  @SuppressWarnings(\"unchecked\")\n" +
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); }\n" +
				"\n" +
				"  public boolean doSomethingElse(Object obj) {\n" +
				"    return false;\n" +
				"  }\n" +
				"}\n" +
				"";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(0, 66));
		regions.add(Pair.of(171, 234));

		String actual = instance.format(text, JavaFormatterOptions.Style.GOOGLE, regions);
		//		Assert.assertEquals("Formatting should change the code", expected, actual);
		// This should be actually equal, if this test fails it means it has been fixed by the google-java-format team
		Assert.assertNotEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testPartlyFormatClassUsingGoogleStyle2() {
		final String text = "package foo; public class Bar {\n" +
				"	public void doSomething(String arg) {\n" +
				"		System.out.println(\"Hello World\");\n" +
				"	}\n" +
				"public boolean doSomethingElse(Object obj) { return false; } }";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				"	public void doSomething(String arg) {\n" +
				"		System.out.println(\"Hello World\");\n" +
				"	}\n" +
				"\n" +
				"  public boolean doSomethingElse(Object obj) {\n" +
				"    return false;\n" +
				"  }\n" +
				"}\n" +
				"";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(0, 31));
		regions.add(Pair.of(111, 173));

		String actual = instance.format(text, JavaFormatterOptions.Style.GOOGLE, regions);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatEnumUsingAndroidStyle() {
		final String text = "package foo;public enum Bar {A,B,C}";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum Bar {\n" +
				"    A,\n" +
				"    B,\n" +
				"    C\n" +
				"}\n"
				+ "";

		String actual = instance.format(text, JavaFormatterOptions.Style.AOSP, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatEnumWithDifferentLineEndingsUsingAndroidStyle() {
		final String text = "package foo;public enum Bar {\r"
				+ "A,\r"
				+ "B,\r"
				+ "C\r"
				+ "}";
		final String expected = "package foo;\r" +
				"\r" +
				"public enum Bar {\r" +
				"    A,\r" +
				"    B,\r" +
				"    C\r" +
				"}\r"
				+ "";

		String actual = instance.format(text, JavaFormatterOptions.Style.AOSP, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testFormatClassUsingAndroidStyle() {
		final String text = "package foo; public class Bar {public void doSomething(String arg) { System.out.println(\"Hello World\"); }}";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				"    public void doSomething(String arg) {\n" +
				"        System.out.println(\"Hello World\");\n" +
				"    }\n" +
				"}\n" +
				"";

		String actual = instance.format(text, JavaFormatterOptions.Style.AOSP, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Currently failing: It seems there is a bug with character ranges in the Google Java Code Formatter
	 * https://github.com/google/google-java-format/issues/433
	 */
	@Test
	public void testPartlyFormatClassUsingAndroidStyle() {
		final String text = "package foo; public class Bar {\n" +
				"    @SuppressWarnings(\"unchecked\")\n" +
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); } public boolean doSomethingElse(Object obj) { return false; } }";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				"    @SuppressWarnings(\"unchecked\")\n" +
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); }\n" +
				"\n" +
				"    public boolean doSomethingElse(Object obj) {\n" +
				"        return false;\n" +
				"    }\n" +
				"}\n" +
				"";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(0, 66));
		regions.add(Pair.of(171, 234));

		String actual = instance.format(text, JavaFormatterOptions.Style.AOSP, regions);
		//		Assert.assertEquals("Formatting should change the code", expected, actual);
		// This should be actually equal, if this test fails it means it has been fixed by the google-java-format team
		Assert.assertNotEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testPartlyFormatClassUsingAndroidStyle2() {
		final String text = "package foo; public class Bar {\n" +
				"	public void doSomething(String arg) {\n" +
				"		System.out.println(\"Hello World\");\n" +
				"	}\n" +
				"public boolean doSomethingElse(Object obj) { return false; } }";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				"	public void doSomething(String arg) {\n" +
				"		System.out.println(\"Hello World\");\n" +
				"	}\n" +
				"\n" +
				"    public boolean doSomethingElse(Object obj) {\n" +
				"        return false;\n" +
				"    }\n" +
				"}\n" +
				"";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(0, 31));
		regions.add(Pair.of(111, 173));

		String actual = instance.format(text, JavaFormatterOptions.Style.AOSP, regions);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNullCode() {
		String actual = instance.format(null, JavaFormatterOptions.Style.AOSP, null);
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}

	@Test
	public void testFormatEnumUsingNullStyle() {
		final String text = "package foo;public enum Bar {A,B,C}";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum Bar {\n" +
				"  A,\n" +
				"  B,\n" +
				"  C\n" +
				"}\n"
				+ "";

		// null code style means default code style which means Google Style
		String actual = instance.format(text, null, null);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testNonFormatableCode() {
		final String text = "package foo;public Bar {A,B,C}";

		try {
			instance.format(text, JavaFormatterOptions.Style.GOOGLE, null);
		} catch (RuntimeException ex) {
			Assert.assertTrue(ex.getMessage(), ex.getMessage().contains("error: class, interface, "));
		}
	}

	@Test
	public void testIllegalChangedElementsCode() {
		final String text = "package foo;public enum Bar {A,B,C}";

		SortedSet<Pair<Integer, Integer>> regions = new TreeSet<>();
		regions.add(Pair.of(1200, 1230));

		try {
			instance.format(text, JavaFormatterOptions.Style.GOOGLE, regions);
		} catch (RuntimeException ex) {
			Assert.assertTrue(ex.getMessage(), ex.getMessage().contains("invalid offset"));
		}
	}
}
