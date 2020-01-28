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

import javax.swing.text.StyledDocument;

import org.junit.Assert;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.NbEditorDocument;

/**
 *
 * @author bahlef
 */
public class GoogleJavaFormatterServiceTest extends NbTestCase {
	public GoogleJavaFormatterServiceTest(String name) {
		super(name);
	}

	/**
	 * Test of {@link GoogleJavaFormatterService#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link GoogleJavaFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormat() throws Exception {
		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}\n";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"  A,\n" +
				"  B,\n" +
				"  C\n" +
				"}\n"
				+ "";

		StyledDocument document = new NbEditorDocument("text/x-java");
		document.insertString(0, text, null);

		GoogleJavaFormatterService instance = new GoogleJavaFormatterService();
		instance.format(document, null);

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Test of {@link GoogleJavaFormatterService#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link GoogleJavaFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testUnsupportedFileType() throws Exception {
		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}\n";

		StyledDocument document = new NbEditorDocument("text/xml");
		document.insertString(0, text, null);

		GoogleJavaFormatterService instance = new GoogleJavaFormatterService();

		try {
			instance.format(document, null);
		} catch (Exception ex) {
			Assert.assertTrue("Formatting should not be possible for the given file type", ex.getMessage().contains("The file type 'text/xml' is not supported"));
		}
	}
}
