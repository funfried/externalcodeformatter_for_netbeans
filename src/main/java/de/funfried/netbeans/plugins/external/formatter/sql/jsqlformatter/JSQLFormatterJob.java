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

import com.manticore.jsqlformatter.JSQLFormatter;
import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang3.tuple.Pair;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

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
		
		TreeMap<String, Object> map = new TreeMap<>();
		map.put(JSQLFormatter.FormattingOption.OUTPUT_FORMAT.toString(), pref.get(JSQLFormatter.FormattingOption.OUTPUT_FORMAT.toString(), JSQLFormatter.getOutputFormat().toString()));
		map.put(JSQLFormatter.FormattingOption.KEYWORD_SPELLING.toString(), pref.get(JSQLFormatter.FormattingOption.KEYWORD_SPELLING.toString(), JSQLFormatter.getKeywordSpelling().toString()));
		map.put(JSQLFormatter.FormattingOption.FUNCTION_SPELLING.toString(), pref.get(JSQLFormatter.FormattingOption.FUNCTION_SPELLING.toString(), JSQLFormatter.getFunctionSpelling().toString()));
		map.put(JSQLFormatter.FormattingOption.OBJECT_SPELLING.toString(), pref.get(JSQLFormatter.FormattingOption.OBJECT_SPELLING.toString(), JSQLFormatter.getObjectSpelling().toString()));
		map.put(JSQLFormatter.FormattingOption.INDENT_WIDTH.toString(), pref.getInt(JSQLFormatter.FormattingOption.INDENT_WIDTH.toString(), JSQLFormatter.getIndentWidth()));
		map.put(JSQLFormatter.FormattingOption.SEPARATION.toString(), pref.get(JSQLFormatter.FormattingOption.SEPARATION.toString(), JSQLFormatter.getSeparation().toString()));
		map.put(JSQLFormatter.FormattingOption.SQUARE_BRACKET_QUOTATION.toString(), pref.get(JSQLFormatter.FormattingOption.SQUARE_BRACKET_QUOTATION.toString(), JSQLFormatter.getSquaredBracketQuotation().toString()));

		String[] options = new String[map.size()];
		int i = 0;
		for (Map.Entry<String, Object> e : map.entrySet()) {
			options[i] = e.getKey() + "=" + e.getValue();
			i++;
		}

		String code = getCode();

		try {
			//@todo: hand over the formatting options
			//@todo: obey the selected region 
			String formattedContent = formatter.format(code, options);
			
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
}
