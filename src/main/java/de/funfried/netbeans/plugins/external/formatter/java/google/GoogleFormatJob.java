/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.google;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import com.google.googlejavaformat.java.JavaFormatterOptions;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Google formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link GoogleJavaFormatterWrapper}.
 *
 * @author bahlef
 */
class GoogleFormatJob extends AbstractFormatJob {
	/** The {@link EclipGoogleFormatterseFormatter} implementation. */
	private final GoogleJavaFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link GoogleFormatJob}.
	 *
	 * @param document        the {@link StyledDocument} which sould be formatted
	 * @param formatter       the {@link GoogleJavaFormatterWrapper} to use
	 * @param changedElements the ranges which should be formatted
	 */
	GoogleFormatJob(StyledDocument document, GoogleJavaFormatterWrapper formatter, SortedSet<Pair<Integer, Integer>> changedElements) {
		super(document, changedElements);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format() throws BadLocationException {
		Preferences pref = Settings.getActivePreferences(document);

		String codeStylePref = pref.get(GoogleJavaFormatterSettings.GOOGLE_FORMATTER_CODE_STYLE, JavaFormatterOptions.Style.GOOGLE.name());

		String code = getCode();

		SortedSet<Pair<Integer, Integer>> regions = getFormatableSections(code);

		try {
			String formattedContent = formatter.format(code, JavaFormatterOptions.Style.valueOf(codeStylePref), regions);
			if (setFormattedCode(code, formattedContent)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Goolge formatter", Icons.ICON_GOOGLE, null, null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using Goolge formatter");
				});
			}
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using Google formatter: " + ex.getMessage());
			});

			throw ex;
		}
	}
}
