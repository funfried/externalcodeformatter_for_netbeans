/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.dbeaver;

import java.util.Properties;
import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * DBeaver SQL formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link DBeaverFormatterWrapper}.
 *
 * @author bahlef
 */
class DBeaverFormatterJob extends AbstractFormatJob {
	/** The DBeaver SQL Formatter implementation. */
	private final DBeaverFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link DBeaverFormatterJob}.
	 *
	 * @param document the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link DBeaverFormatterWrapper} to use
	 * @param changedElements the ranges which should be formatted
	 */
	DBeaverFormatterJob(StyledDocument document, DBeaverFormatterWrapper formatter, SortedSet<Pair<Integer, Integer>> changedElements) {
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
			String formattedContent = formatter.format(code, getProperties(pref));

			if (setFormattedCode(code, formattedContent)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using DBeaver SQL formatter", Icons.ICON_DBEAVER, "", null);
					}
					StatusDisplayer.getDefault().setStatusText("Format using DBeaver SQL formatter");
				});
			}
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using DBeaver SQL formatter: " + ex.getMessage());
			});

			throw ex;
		}
	}

	private Properties getProperties(Preferences pref) {
		Properties props = new Properties();

		props.put(DBeaverFormatterSettings.INDENT_SIZE, Integer.toString(pref.getInt(DBeaverFormatterSettings.INDENT_SIZE, DBeaverFormatterSettings.INDENT_SIZE_DEFAULT)));
		props.put(DBeaverFormatterSettings.INDENT_TYPE, pref.get(DBeaverFormatterSettings.INDENT_TYPE, DBeaverFormatterSettings.INDENT_TYPE_DEFAULT));
		props.put(DBeaverFormatterSettings.KEYWORD_CASE, pref.get(DBeaverFormatterSettings.KEYWORD_CASE, DBeaverFormatterSettings.KEYWORD_CASE_DEFAULT));
		props.put(DBeaverFormatterSettings.STATEMENT_DELIMITER, pref.get(DBeaverFormatterSettings.STATEMENT_DELIMITER, DBeaverFormatterSettings.STATEMENT_DELIMITER_DEFAULT));

		return props;
	}
}
