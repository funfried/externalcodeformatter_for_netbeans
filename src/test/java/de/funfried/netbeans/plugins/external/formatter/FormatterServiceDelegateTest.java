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

import java.util.prefs.Preferences;

import javax.swing.text.StyledDocument;

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
		String mimeType = "text/x-java";
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.ENABLED_FORMATTER_PREFIX + mimeType, EclipseJavaFormatterService.ID);

		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}\n";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"    A, B, C\n" +
				"}\n" +
				"";

		StyledDocument document = new NbEditorDocument(mimeType);
		document.insertString(0, text, null);

		Assert.assertEquals((long) 120L, (long) FormatterServiceDelegate.getInstance().getRightMargin(document));

		Assert.assertEquals((long) 2L, (long) FormatterServiceDelegate.getInstance().getContinuationIndentSize(document));
		Assert.assertEquals((long) 4L, (long) FormatterServiceDelegate.getInstance().getIndentSize(document));
		Assert.assertEquals((long) 4L, (long) FormatterServiceDelegate.getInstance().getSpacesPerTab(document));
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
		String mimeType = "text/x-java";
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.ENABLED_FORMATTER_PREFIX + mimeType, GoogleJavaFormatterService.ID);

		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}\n";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"  A,\n" +
				"  B,\n" +
				"  C\n" +
				"}\n"
				+ "";

		StyledDocument document = new NbEditorDocument(mimeType);
		document.insertString(0, text, null);

		FormatterServiceDelegate.getInstance().format(document, null);

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}
}
