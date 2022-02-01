/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.sqlformatter;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import com.github.vertical_blank.sqlformatter.languages.Dialect;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Vertical Blank SQL formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link SQLFormatterWrapper}.
 *
 * @author bahlef
 */
class SQLFormatterJob extends AbstractFormatJob {
	/** The Vertical Blank SQL Formatter implementation. */
	private final SQLFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link SQLFormatterJob}.
	 *
	 * @param document the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link SQLFormatterWrapper} to use
	 * @param changedElements the ranges which should be formatted
	 */
	SQLFormatterJob(StyledDocument document, SQLFormatterWrapper formatter, SortedSet<Pair<Integer, Integer>> changedElements) {
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
			String formattedContent = formatter.format(code, getDialect(pref), getFormatConfig(pref));

			if (setFormattedCode(code, formattedContent)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Vertical Blank SQL formatter", Icons.ICON_EXTERNAL, "", null);
					}
					StatusDisplayer.getDefault().setStatusText("Format using Vertical Blank SQL formatter");
				});
			}
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using Vertical Blank SQL formatter: " + ex.getMessage());
			});

			throw ex;
		}
	}

	private Dialect getDialect(Preferences pref) {
		return Dialect.valueOf(pref.get(SQLFormatterSettings.DIALECT, SQLFormatterSettings.DIALECT_DEFAULT));
	}

	private FormatConfig getFormatConfig(Preferences pref) {
		int indentSize = pref.getInt(SQLFormatterSettings.INDENT_SIZE, SQLFormatterSettings.INDENT_SIZE_DEFAULT);
		char indentSign = pref.getBoolean(SQLFormatterSettings.EXPAND_TABS_TO_SPACES, SQLFormatterSettings.EXPAND_TABS_TO_SPACES_DEFAULT) ? ' ' : '\t';

		String indent = StringUtils.repeat(indentSign, indentSize);

		return FormatConfig.builder()
				.indent(indent)
				.linesBetweenQueries(pref.getInt(SQLFormatterSettings.LINES_BETWEEN_QUERIES, SQLFormatterSettings.LINES_BETWEEN_QUERIES_DEFAULT))
				.maxColumnLength(pref.getInt(SQLFormatterSettings.MAX_COLUMN_LENGTH, SQLFormatterSettings.MAX_COLUMN_LENGTH_DEFAULT))
				.uppercase(pref.getBoolean(SQLFormatterSettings.UPPERCASE, SQLFormatterSettings.UPPERCASE_DEFAULT))
				.build();
	}
}
