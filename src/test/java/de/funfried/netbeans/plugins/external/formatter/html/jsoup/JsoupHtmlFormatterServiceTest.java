/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.html.jsoup;

import java.util.prefs.Preferences;

import javax.swing.text.StyledDocument;

import org.junit.Assert;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.NbEditorDocument;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import de.funfried.netbeans.plugins.external.formatter.xml.jsoup.*;

/**
 *
 * @author bahlef
 */
public class JsoupHtmlFormatterServiceTest extends NbTestCase {
	public JsoupHtmlFormatterServiceTest(String name) {
		super(name);
	}

	/**
	 * Test of format method, of class {@link JsoupHtmlFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormat() throws Exception {
		final String text = "<!DOCTYPE html><html><head><title>foobar</title></head><body width=\"100%\"><div><p>some text</p><img src=\"../img.png\" alt=\"Some image\"></div></body></html>\n";
		final String expected = "<!doctype html>\n"
				+ "<html>\n"
				+ " <head>\n"
				+ "  <title>foobar</title>\n"
				+ " </head>\n"
				+ " <body width=\"100%\">\n"
				+ "  <div>\n"
				+ "   <p>some text</p>\n"
				+ "   <img src=\"../img.png\" alt=\"Some image\">\n"
				+ "  </div>\n"
				+ " </body>\n"
				+ "</html>\n"
				+ "";

		StyledDocument document = new NbEditorDocument("text/html");
		document.insertString(0, text, null);

		Preferences prefs = Settings.getActivePreferences(document);
		prefs.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		prefs.putBoolean(Settings.OVERRIDE_TAB_SIZE, false);

		JsoupHtmlFormatterService instance = new JsoupHtmlFormatterService();
		Assert.assertEquals("Jsoup HTML Code Formatter", instance.getDisplayName());
		Assert.assertNotNull(instance.createOptionsPanel(null));
		Assert.assertEquals((long) 0L, (long) instance.getRightMargin(document));

		Assert.assertEquals((long) 1L, (long) instance.getContinuationIndentSize(document));
		Assert.assertEquals((long) 1L, (long) instance.getIndentSize(document));
		Assert.assertEquals((long) 1L, (long) instance.getSpacesPerTab(document));
		Assert.assertTrue(instance.isExpandTabToSpaces(document));

		Assert.assertNull(instance.getContinuationIndentSize(null));
		Assert.assertNull(instance.getIndentSize(null));
		Assert.assertNull(instance.getSpacesPerTab(null));
		Assert.assertNull(instance.isExpandTabToSpaces(null));

		prefs.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, false);

		Assert.assertNull(instance.getContinuationIndentSize(document));
		Assert.assertNull(instance.getIndentSize(document));
		Assert.assertNull(instance.getSpacesPerTab(document));
		Assert.assertNull(instance.isExpandTabToSpaces(document));

		instance.format(document, null);

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Test of {@link JsoupXmlFormatterService#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link JsoupXmlFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testUnsupportedFileType() throws Exception {
		final String text = "<html><head><title>foobar</title></head><body width=\"100%\"><div><p>val</p><img src=\"../img.png\" alt=\"Some image\"></div></body></html>\n";

		StyledDocument document = new NbEditorDocument("text/javascript");
		document.insertString(0, text, null);

		JsoupXmlFormatterService instance = new JsoupXmlFormatterService();

		try {
			instance.format(document, null);

			Assert.assertFalse("Formatting should not be possible for the given file type!", true);
		} catch (Exception ex) {
			Assert.assertTrue("Formatting should not be possible for the given file type", ex.getMessage().contains("The file type 'text/javascript' is not supported"));
		}
	}
}
