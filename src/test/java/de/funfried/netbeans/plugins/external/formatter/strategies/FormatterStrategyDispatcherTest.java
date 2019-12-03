/*
 * Copyright (c) 2019 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import java.util.prefs.Preferences;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import org.junit.Assert;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.openide.util.NbPreferences;

import de.funfried.netbeans.plugins.external.formatter.ui.options.ExternalFormatterPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class FormatterStrategyDispatcherTest extends NbTestCase {
	public FormatterStrategyDispatcherTest() {
		super(FormatterStrategyDispatcherTest.class.getSimpleName());
	}

	/**
	 * Test of {@link FormatterStrategyDispatcher#format(de.funfried.netbeans.plugins.external.formatter.strategies.FormatterAdvice)}
	 * method, of class {@link FormatterStrategyDispatcher}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormatWithEclipseFormatter() throws Exception {
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.ECLIPSE_FORMATTER_ENABLED, "true");
		prefs.put(Settings.GOOGLE_FORMATTER_ENABLED, "false");

		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"    A, B, C\n" +
				"}";

		StyledDocument document = new DefaultStyledDocument();
		document.putProperty("mimeType", "text/x-java");
		document.insertString(0, text, null);

		FormatterStrategyDispatcher.getInstance().format(new FormatterAdvice(document, null, -1, null));

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Test of {@link FormatterStrategyDispatcher#format(de.funfried.netbeans.plugins.external.formatter.strategies.FormatterAdvice)}
	 * method, of class {@link FormatterStrategyDispatcher}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormatWithGoogleFormatter() throws Exception {
		Preferences prefs = NbPreferences.forModule(ExternalFormatterPanel.class);
		prefs.put(Settings.GOOGLE_FORMATTER_ENABLED, "true");
		prefs.put(Settings.ECLIPSE_FORMATTER_ENABLED, "false");

		final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
		final String expected = "package foo;\n" +
				"\n" +
				"public enum NewEmptyJUnitTest {\n" +
				"  A,\n" +
				"  B,\n" +
				"  C\n" +
				"}\n"
				+ "";

		StyledDocument document = new DefaultStyledDocument();
		document.putProperty("mimeType", "text/x-java");
		document.insertString(0, text, null);

		FormatterStrategyDispatcher.getInstance().format(new FormatterAdvice(document, null, -1, null));

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}
}
