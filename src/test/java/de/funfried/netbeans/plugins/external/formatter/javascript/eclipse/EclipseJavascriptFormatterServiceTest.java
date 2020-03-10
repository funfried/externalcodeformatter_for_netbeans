/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.javascript.eclipse;

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
public class EclipseJavascriptFormatterServiceTest extends NbTestCase {
	public EclipseJavascriptFormatterServiceTest(String name) {
		super(name);
	}

	/**
	 * Test of format method, of class {@link EclipseJavascriptFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFormat() throws Exception {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }\n";
		final String expected = "function foo(bar) {\n"
				+ "    return Str('', {\n"
				+ "        '' : bar\n"
				+ "    }, []);\n"
				+ "}\n";

		StyledDocument document = new NbEditorDocument("text/javascript");
		document.insertString(0, text, null);

		EclipseJavascriptFormatterService instance = new EclipseJavascriptFormatterService();
		Assert.assertEquals("Eclipse Javascript Code Formatter", instance.getDisplayName());
		Assert.assertNotNull(instance.getOptionsPanel());
		Assert.assertEquals((long) 80L, (long) instance.getRightMargin(document));

		Assert.assertEquals((long) 2L, (long) instance.getContinuationIndentSize(document));
		Assert.assertEquals((long) 4L, (long) instance.getIndentSize(document));
		Assert.assertEquals((long) 4L, (long) instance.getSpacesPerTab(document));
		Assert.assertTrue(instance.isExpandTabToSpaces(document));

		Assert.assertNull(instance.getContinuationIndentSize(null));
		Assert.assertNull(instance.getIndentSize(null));
		Assert.assertNull(instance.getSpacesPerTab(null));
		Assert.assertNull(instance.isExpandTabToSpaces(null));

		Preferences prefs = Settings.getActivePreferences(document);
		prefs.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, false);
		prefs.flush();

		Assert.assertNull(instance.getContinuationIndentSize(document));
		Assert.assertNull(instance.getIndentSize(document));
		Assert.assertNull(instance.getSpacesPerTab(document));
		Assert.assertNull(instance.isExpandTabToSpaces(document));

		instance.format(document, null);

		String actual = document.getText(0, document.getLength());

		Assert.assertEquals("Formatting should change the code", expected, actual);
	}

	/**
	 * Test of {@link EclipseJavascriptFormatterService#format(javax.swing.text.StyledDocument, java.util.SortedSet)}
	 * method, of class {@link EclipseJavascriptFormatterService}.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testUnsupportedFileType() throws Exception {
		final String text = "function foo(bar) { return Str('', { '' : bar }, []); }\n";

		StyledDocument document = new NbEditorDocument("text/xml");
		document.insertString(0, text, null);

		EclipseJavascriptFormatterService instance = new EclipseJavascriptFormatterService();

		try {
			instance.format(document, null);
		} catch (Exception ex) {
			Assert.assertTrue("Formatting should not be possible for the given file type", ex.getMessage().contains("The file type 'text/xml' is not supported"));
		}
	}
}
