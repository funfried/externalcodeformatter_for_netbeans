/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import com.manticore.jsqlformatter.JSQLFormatter;
import com.manticore.jsqlformatter.JSQLFormatter.FormattingOption;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * JSQLFormatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link JSQLFormatterWrapper}.
 *
 * @author Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 */
class JSQLFormatterJob extends AbstractFormatJob {
	/** The {@link JSQLFormatter} implementation. */
	private final JSQLFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link JSQLFormatterJob}.
	 *
	 * @param document the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link JSQLFormatterWrapper} to use
	 * @param changedElements the ranges which should be formatted
	 */
	JSQLFormatterJob(StyledDocument document, JSQLFormatterWrapper formatter, SortedSet<Pair<Integer, Integer>> changedElements) {
		super(document, changedElements);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format() throws BadLocationException {
		Preferences pref = Settings.getActivePreferences(document);

		String code = getCode();

		try {
			//@todo: hand over the formatting options
			//@todo: obey the selected region
			String formattedContent = formatter.format(code, getOptions(pref));

			if (setFormattedCode(code, formattedContent)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using JSQLFormatter", Icons.ICON_MANTICORE, "", null);
					}
					StatusDisplayer.getDefault().setStatusText("Format using JSQLFormatter");
				});
			}
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using JSQLFormatter formatter: " + ex.getMessage());
			});

			throw ex;
		}
	}

	private String[] getOptions(Preferences pref) {
		int i = 0;
		String[] options = new String[FormattingOption.values().length];
		options[i++] = toOption(pref, FormattingOption.OUTPUT_FORMAT, JSQLFormatter.getOutputFormat());
		options[i++] = toOption(pref, FormattingOption.KEYWORD_SPELLING, JSQLFormatter.getKeywordSpelling());
		options[i++] = toOption(pref, FormattingOption.FUNCTION_SPELLING, JSQLFormatter.getFunctionSpelling());
		options[i++] = toOption(pref, FormattingOption.OBJECT_SPELLING, JSQLFormatter.getObjectSpelling());
		options[i++] = toOption(pref, FormattingOption.INDENT_WIDTH, JSQLFormatter.getIndentWidth());
		options[i++] = toOption(pref, FormattingOption.SEPARATION, JSQLFormatter.getSeparation());
		options[i++] = toOption(pref, FormattingOption.SQUARE_BRACKET_QUOTATION, JSQLFormatter.getSquaredBracketQuotation());

		return options;
	}

	private <E extends Enum<E>> String toOption(Preferences pref, JSQLFormatter.FormattingOption option, E defaultValue) {
		return toOption(pref, option, String.valueOf(defaultValue));
	}

	private String toOption(Preferences pref, JSQLFormatter.FormattingOption option, String defaultValue) {
		return option.toString() + "=" + pref.get(option.toString(), defaultValue);
	}

	private String toOption(Preferences pref, JSQLFormatter.FormattingOption option, int defaultValue) {
		return String.valueOf(option) + "=" + pref.getInt(String.valueOf(option), defaultValue);
	}
}
