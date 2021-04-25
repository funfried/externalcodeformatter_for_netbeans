/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import java.util.SortedSet;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang3.tuple.Pair;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

/**
 * Google formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link JSQLFormatterWrapper}.
 *
 * @author bahlef
 */
class JSQLFormatterJob extends AbstractFormatJob {
	/** The {@link EclipGoogleFormatterseFormatter} implementation. */
	private final JSQLFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link GoogleFormatJob}.
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

//		String codeStylePref = pref.get(JSQLFormatterSettings.SQL_FORMATTER_CODE_STYLE, JavaFormatterOptions.Style.GOOGLE.name());
		String code = getCode();
//		SortedSet<Pair<Integer, Integer>> regions = getFormatableSections(code);

		try {
			//@todo: hand over the formatting options
			//@todo: obey the selected region 
			String formattedContent = formatter.format(code);
			if (setFormattedCode(code, formattedContent)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using JSQLFormatter formatter", Icons.ICON_GOOGLE, "", null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using JSQLFormatter formatter");
				});
			}
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using JSQLFormatter formatter: " + ex.getMessage());
			});

			throw ex;
		}
	}
}
