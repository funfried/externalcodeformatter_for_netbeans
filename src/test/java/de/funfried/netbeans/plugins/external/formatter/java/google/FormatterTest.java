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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Range;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.JavaFormatterOptions;

/**
 *
 * @author bahlef
 */
public class FormatterTest {
	public FormatterTest() {
	}

	@Test
	@Ignore
	public void testCharaterRangesFormatWithAnnotation() throws Exception {
		final String text = "package foo; public class Bar {\n" +
				"    @SuppressWarnings(\"unchecked\")\n" +
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); } public boolean doSomethingElse(Object obj) { return false; } }";
		final String expected = "package foo;\n" +
				"\n" +
				"public class Bar {\n" +
				// START -- this should not be formatted
				"  @SuppressWarnings(\"unchecked\")\n" +
				"    // some comment      \n" +
				"    public void doSomething(String arg) { System.out.println(\"Hello World\"); }\n" +
				// END -- this should not be formatted
				"\n" +
				"  public boolean doSomethingElse(Object obj) {\n" +
				"    return false;\n" +
				"  }\n" +
				"}\n" +
				"";

		Collection<Range<Integer>> characterRanges = new ArrayList<>();
		characterRanges.add(Range.open(0, 66));
		characterRanges.add(Range.open(171, 234));

		Formatter formatter = new Formatter(JavaFormatterOptions.builder().style(JavaFormatterOptions.Style.GOOGLE).build());

		String actual = formatter.formatSource(text, characterRanges);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	@Test
	public void testCharaterRangesFormatWithoutAnnotation() throws Exception {
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
				"  public boolean doSomethingElse(Object obj) {\n" +
				"    return false;\n" +
				"  }\n" +
				"}\n" +
				"";

		Collection<Range<Integer>> characterRanges = new ArrayList<>();
		characterRanges.add(Range.open(0, 31));
		characterRanges.add(Range.open(171, 234));

		Formatter formatter = new Formatter(JavaFormatterOptions.builder().style(JavaFormatterOptions.Style.GOOGLE).build());

		String actual = formatter.formatSource(text, characterRanges);
		Assert.assertEquals("Formatting should change the code", expected, actual);
	}
}
