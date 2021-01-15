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

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.netbeans.editor.BaseDocument;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Jackson formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link JacksonJsonFormatterWrapper}.
 *
 * @author bahlef
 */
class JacksonFormatJob extends AbstractFormatJob {
	/** * The {@link JacksonJsonFormatterWrapper} implementation. */
	private final JacksonJsonFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link JacksonFormatJob}.
	 *
	 * @param document the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link JacksonJsonFormatterWrapper} to use
	 */
	JacksonFormatJob(StyledDocument document, JacksonJsonFormatterWrapper formatter) {
		super(document, null);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format() throws BadLocationException {
		Preferences pref = Settings.getActivePreferences(document);

		int indentSize = pref.getInt(JacksonJsonFormatterSettings.INDENT_SIZE, 2);
		int spacesPerTab = pref.getInt(JacksonJsonFormatterSettings.SPACES_PER_TAB, 2);
		boolean expandTabToSpaces = pref.getBoolean(JacksonJsonFormatterSettings.EXPAND_TABS_TO_SPACES, true);
		boolean spacesBeforeSeparator = pref.getBoolean(JacksonJsonFormatterSettings.SPACE_BEFORE_SEPARATOR, false);
		String lineFeedSetting = pref.get(JacksonJsonFormatterSettings.LINEFEED, "");

		JacksonJsonFormatterWrapper.Options options = new JacksonJsonFormatterWrapper.Options();
		options.setIndentSize(indentSize);
		options.setSpacesPerTab(spacesPerTab);
		options.setExpandTabsToSpaces(expandTabToSpaces);
		options.setSpaceBeforeSeparator(spacesBeforeSeparator);

		//save with configured linefeed
		String lineFeed = Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
		if (null != lineFeed) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		String code = getCode();

		try {
			String formattedContent = formatter.format(code, lineFeed, options);
			if (setFormattedCode(code, formattedContent)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Jackson Json formatter", Icons.ICON_JACKSON, "", null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using Jackson Json formatter");
				});
			}
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using Jackson Json formatter: " + ex.getMessage());
			});

			throw ex;
		}
	}
}
