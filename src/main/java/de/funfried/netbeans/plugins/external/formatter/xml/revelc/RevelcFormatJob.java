/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.xml.revelc;

import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.netbeans.editor.BaseDocument;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import net.revelc.code.formatter.xml.lib.FormattingPreferences;

/**
 * revelc.net formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link RevelcXmlFormatterWrapper}.
 *
 * @author bahlef
 */
class RevelcFormatJob extends AbstractFormatJob {
	/** The {@link RevelcXmlFormatterWrapper} implementation. */
	private final RevelcXmlFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link RevelcFormatJob}.
	 *
	 * @param document  the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link RevelcXmlFormatterWrapper} to use
	 */
	RevelcFormatJob(StyledDocument document, RevelcXmlFormatterWrapper formatter) {
		super(document, null);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format() throws BadLocationException {
		Preferences pref = Settings.getActivePreferences(document);

		boolean tabInsteadOfSpaces = pref.getBoolean(RevelcXmlFormatterSettings.TAB_INSTEAD_OF_SPACES, true);
		boolean splitMultiAttrs = pref.getBoolean(RevelcXmlFormatterSettings.SPLIT_MULTI_ATTRIBUTES, false);
		boolean wrapLongLines = pref.getBoolean(RevelcXmlFormatterSettings.WRAP_LONG_LINES, true);
		int tabWidth = pref.getInt(RevelcXmlFormatterSettings.TAB_WIDTH, 4);
		int maxLineLength = pref.getInt(RevelcXmlFormatterSettings.MAX_LINE_LENGTH, 120);
		String wellFormedValidation = pref.get(RevelcXmlFormatterSettings.WELL_FORMED_VALIDATION, FormattingPreferences.WARN);
		String lineFeedSetting = pref.get(RevelcXmlFormatterSettings.LINEFEED, "");

		FormattingPreferences prefs = new FormattingPreferences();
		prefs.setMaxLineLength(maxLineLength);
		prefs.setSplitMultiAttrs(splitMultiAttrs);
		prefs.setTabInsteadOfSpaces(tabInsteadOfSpaces);
		prefs.setTabWidth(tabWidth);
		prefs.setWellFormedValidation(wellFormedValidation);
		prefs.setWrapLongLines(wrapLongLines);

		//save with configured linefeed
		String lineFeed = Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
		if (null != lineFeed) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		String code = getCode();

		String formattedContent = formatter.format(code, lineFeed, prefs);
		if (setFormattedCode(code, formattedContent)) {
			SwingUtilities.invokeLater(() -> {
				if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
					NotificationDisplayer.getDefault().notify("Format using revelc XML formatter", Icons.ICON_REVELC, null, null);
				}

				StatusDisplayer.getDefault().setStatusText("Format using revelc XML formatter");
			});
		}
	}
}
