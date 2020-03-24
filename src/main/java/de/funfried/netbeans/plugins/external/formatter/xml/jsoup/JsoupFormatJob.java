/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.xml.jsoup;

import java.nio.charset.Charset;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.jsoup.nodes.Document;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Jsoup formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link JsoupXmlFormatterWrapper}.
 *
 * @author bahlef
 */
class JsoupFormatJob extends AbstractFormatJob {
	/** * The {@link JsoupXmlFormatterWrapper} implementation. */
	private final JsoupXmlFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link JsoupFormatJob}.
	 *
	 * @param document  the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link JsoupXmlFormatterWrapper} to use
	 */
	JsoupFormatJob(StyledDocument document, JsoupXmlFormatterWrapper formatter) {
		super(document, null);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format() throws BadLocationException {
		Preferences pref = Settings.getActivePreferences(document);

		boolean prettyPrint = pref.getBoolean(JsoupXmlFormatterSettings.PRETTY_PRINT, true);
		boolean outline = pref.getBoolean(JsoupXmlFormatterSettings.OUTLINE, false);
		int indentSize = pref.getInt(JsoupXmlFormatterSettings.INDENT_SIZE, 1);
		String lineFeedSetting = pref.get(JsoupXmlFormatterSettings.LINEFEED, "");

		Document.OutputSettings options = new Document.OutputSettings();
		options.indentAmount(indentSize);
		options.outline(outline);
		options.prettyPrint(prettyPrint);

		FileObject fileObj = NbEditorUtilities.getFileObject(document);
		if (fileObj != null) {
			Charset charset = FileEncodingQuery.getEncoding(fileObj);
			if (charset != null) {
				options.charset(charset);
			}
		}

		//save with configured linefeed
		String lineFeed = Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
		if (null != lineFeed) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		String code = getCode();

		String formattedContent = formatter.format(code, lineFeed, options);
		if (setFormattedCode(code, formattedContent)) {
			SwingUtilities.invokeLater(() -> {
				if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
					NotificationDisplayer.getDefault().notify("Format using Jsoup XML formatter", Icons.ICON_JSOUP, null, null);
				}

				StatusDisplayer.getDefault().setStatusText("Format using Jsoup XML formatter");
			});
		}
	}
}
