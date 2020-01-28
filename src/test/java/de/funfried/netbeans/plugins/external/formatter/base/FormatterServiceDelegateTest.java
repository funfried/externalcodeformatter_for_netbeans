/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.base;

import java.util.prefs.Preferences;

import javax.swing.text.StyledDocument;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.util.NbPreferences;

import de.funfried.netbeans.plugins.external.formatter.java.eclipse.EclipseJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.java.google.GoogleJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.ui.options.ExternalFormatterPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Todo: Fix test
 * It seems none of the FormatterServices is registered in the default lookup while the tests are running.
 *
 * @author bahlef
 */
@Ignore
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
		prefs.put(Settings.ENABLED_FORMATTER, EclipseJavaFormatterService.ID);

		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}\n";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"    A, B, C\n" +
				"}\n" +
				"";

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
	public void testFormatWithGoogleFormatter() throws Exception {
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.ENABLED_FORMATTER, GoogleJavaFormatterService.ID);

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
}
