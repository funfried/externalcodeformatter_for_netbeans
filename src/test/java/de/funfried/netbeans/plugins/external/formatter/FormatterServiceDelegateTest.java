/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.util.NbPreferences;

import de.funfried.netbeans.plugins.external.formatter.java.eclipse.EclipseJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.java.google.GoogleJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.ui.options.ExternalFormatterPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class FormatterServiceDelegateTest extends NbTestCase {
	public FormatterServiceDelegateTest(String name) {
		super(name);
	}

	/**
	 * Test of {@link FormatterServiceDelegate#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link FormatterServiceDelegate}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormatWithEclipseFormatter() throws Exception {
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.ENABLED_FORMATTER_PREFIX + MimeType.JAVA.toString(), EclipseJavaFormatterService.ID);
		prefs.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		prefs.putBoolean(Settings.OVERRIDE_TAB_SIZE, false);

		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}\n";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"    A, B, C\n" +
				"}\n" +
				"";

		StyledDocument document = new NbEditorDocument("text/x-java");
		document.insertString(0, text, null);

		Assert.assertEquals((long) 120L, (long) FormatterServiceDelegate.getInstance().getRightMargin(document));

		Assert.assertEquals((long) 2L, (long) FormatterServiceDelegate.getInstance().getContinuationIndentSize(document));
		Assert.assertEquals((long) 4L, (long) FormatterServiceDelegate.getInstance().getIndentSize(document));
		Assert.assertEquals((long) 8L, (long) FormatterServiceDelegate.getInstance().getSpacesPerTab(document));
		Assert.assertFalse(FormatterServiceDelegate.getInstance().isExpandTabToSpaces(document));

		Assert.assertNull(FormatterServiceDelegate.getInstance().getContinuationIndentSize(null));
		Assert.assertNull(FormatterServiceDelegate.getInstance().getIndentSize(null));
		Assert.assertNull(FormatterServiceDelegate.getInstance().getSpacesPerTab(null));
		Assert.assertNull(FormatterServiceDelegate.getInstance().isExpandTabToSpaces(null));

		prefs.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, false);

		Assert.assertNull(FormatterServiceDelegate.getInstance().getContinuationIndentSize(document));
		Assert.assertNull(FormatterServiceDelegate.getInstance().getIndentSize(document));
		Assert.assertNull(FormatterServiceDelegate.getInstance().getSpacesPerTab(document));
		Assert.assertNull(FormatterServiceDelegate.getInstance().isExpandTabToSpaces(document));

		FormatterServiceDelegate.getInstance().format(document, null);

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Test of {@link FormatterServiceDelegate#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link FormatterServiceDelegate}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormatWithGoogleFormatter() throws Exception {
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.ENABLED_FORMATTER_PREFIX + MimeType.JAVA.toString(), GoogleJavaFormatterService.ID);

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

		FormatterServiceDelegate.getInstance().format(document, null);

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Test of {@link FormatterServiceDelegate#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link FormatterServiceDelegate}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormatWithWrongChangedElements() throws Exception {
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.ENABLED_FORMATTER_PREFIX + MimeType.JAVA.toString(), GoogleJavaFormatterService.ID);

		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}\n";

		StyledDocument document = new NbEditorDocument("text/x-java");
		document.insertString(0, text, null);

		SortedSet<Pair<Integer, Integer>> changedElements = new TreeSet<>();
		changedElements.add(Pair.of(-1, 100000));

		Assert.assertTrue(FormatterServiceDelegate.getInstance().format(document, changedElements));

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting shouldn't change the code", text, actual);
	}

	/**
	 * Test of {@link FormatterServiceDelegate#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link FormatterServiceDelegate}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testNullDocument() throws Exception {
		Assert.assertFalse("Formatting should not be possible for a null document!", FormatterServiceDelegate.getInstance().format(null, null));
	}
}
