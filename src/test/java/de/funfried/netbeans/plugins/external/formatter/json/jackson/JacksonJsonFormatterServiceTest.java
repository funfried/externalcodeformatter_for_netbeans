/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.json.jackson;

import java.util.prefs.Preferences;

import javax.swing.text.StyledDocument;

import org.junit.Assert;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.NbEditorDocument;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class JacksonJsonFormatterServiceTest extends NbTestCase {
	public JacksonJsonFormatterServiceTest(String name) {
		super(name);
	}

	/**
	 * Test of format method, of class {@link JacksonJsonFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormat() throws Exception {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}\n";
		final String expected = "{\n" +
				"  \"foo\": \"bar\",\n" +
				"  \"array\": [\n" +
				"    \"value1\",\n" +
				"    \"value2\",\n" +
				"    \"value3\"\n" +
				"  ],\n" +
				"  \"object\": {\n" +
				"    \"string\": \"value\",\n" +
				"    \"boolean\": false,\n" +
				"    \"number\": 2\n" +
				"  }\n" +
				"}\n" +
				"";

		StyledDocument document = new NbEditorDocument("text/x-json");
		document.insertString(0, text, null);

		Preferences prefs = Settings.getActivePreferences(document);
		prefs.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		prefs.putBoolean(Settings.OVERRIDE_TAB_SIZE, false);

		JacksonJsonFormatterService instance = new JacksonJsonFormatterService();
		Assert.assertEquals("Jackson Json Code Formatter", instance.getDisplayName());
		Assert.assertNotNull(instance.getOptionsPanel());
		Assert.assertEquals((long) 0L, (long) instance.getRightMargin(document));

		Assert.assertEquals((long) 2L, (long) instance.getContinuationIndentSize(document));
		Assert.assertEquals((long) 2L, (long) instance.getIndentSize(document));
		Assert.assertEquals((long) 2L, (long) instance.getSpacesPerTab(document));
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
	 * Test of {@link JacksonJsonFormatterService#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link JacksonJsonFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testUnsupportedFileType() throws Exception {
		final String text = "{\"foo\":\"bar\", \"array\": [ \"value1\", \"value2\", \"value3\" ], \"object\": {\"string\": \"value\", \"boolean\": false, \"number\": 2}}\n";

		StyledDocument document = new NbEditorDocument("text/xml");
		document.insertString(0, text, null);

		JacksonJsonFormatterService instance = new JacksonJsonFormatterService();

		try {
			instance.format(document, null);

			Assert.assertFalse("Formatting should not be possible for the given file type!", true);
		} catch (Exception ex) {
			Assert.assertTrue("Formatting should not be possible for the given file type", ex.getMessage().contains("The file type 'text/xml' is not supported"));
		}
	}
}
