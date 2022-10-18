/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.css.cssparser;

import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Google formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link CssParserFormatterWrapper}.
 *
 * @author bahlef
 */
class CssParserFormatJob extends AbstractFormatJob {
	/** * The {@link CssParserFormatterWrapper} implementation. */
	private final CssParserFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link CssParserFormatJob}.
	 *
	 * @param document the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link CssParserFormatterWrapper} to use
	 */
	CssParserFormatJob(StyledDocument document, CssParserFormatterWrapper formatter) {
		super(document, null);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format() throws BadLocationException {
		Preferences pref = Settings.getActivePreferences(document);

		int indentPref = pref.getInt(CssParserFormatterSettings.INDENT, CssParserFormatterSettings.INDENT_DEFAULT);
		boolean rgbAsHexPref = pref.getBoolean(CssParserFormatterSettings.RGB_AS_HEX, CssParserFormatterSettings.RGB_AS_HEX_DEFAULT);
		boolean useSingleQuotesPref = pref.getBoolean(CssParserFormatterSettings.USE_SINGLE_QUOTES, CssParserFormatterSettings.USE_SINGLE_QUOTES_DEFAULT);
		boolean useSourceStringValuesPref = pref.getBoolean(CssParserFormatterSettings.USE_SOURCE_STRING_VALUES, CssParserFormatterSettings.USE_SOURCE_STRING_VALUES_DEFAULT);

		String code = getCode();

		try {
			String formattedContent = formatter.format(code, indentPref, rgbAsHexPref, useSingleQuotesPref, useSourceStringValuesPref);

			if (setFormattedCode(code, formattedContent)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using CssParser", Icons.ICON_EXTERNAL, "", null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using CssParser");
				});
			}
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using CssParser: " + ex.getMessage());
			});

			throw ex;
		}
	}
}
