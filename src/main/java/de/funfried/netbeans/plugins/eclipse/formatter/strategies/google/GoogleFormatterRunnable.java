/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.eclipse.formatter.strategies.google;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;

import com.google.googlejavaformat.java.JavaFormatterOptions;

import de.funfried.netbeans.plugins.eclipse.formatter.Utils;
import de.funfried.netbeans.plugins.eclipse.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.eclipse.formatter.options.Settings;
import de.funfried.netbeans.plugins.eclipse.formatter.strategies.AbstractFormatterRunnable;

/**
 * Formats the given document using the google formatter. LineBreakpoints get
 * removed and the following breakpoints are getting reattached:
 * <ul>
 * <li>ClassLoadUnloadBreakpoint</li>
 * <li>FieldBreakpoint</li>
 * <li>MethodBreakpoint</li>
 * </ul>
 *
 * @author bahlef
 */
class GoogleFormatterRunnable extends AbstractFormatterRunnable {
	private static final Logger log = Logger.getLogger(GoogleFormatterRunnable.class.getName());

	private final GoogleFormatter formatter;

	GoogleFormatterRunnable(StyledDocument document, GoogleFormatter formatter, int dot, int mark, SortedSet<Pair<Integer, Integer>> changedElements) {
		super(document, dot, mark, changedElements);

		this.formatter = formatter;
	}

	@Override
	public void run() {
		boolean isJava = Utils.isJava(document);
		if (!isJava) {
			throw new FileTypeNotSupportedException("The file type '" + NbEditorUtilities.getMimeType(document) + "' is not supported by the Goolge Java Code Formatter");
		}

		Preferences pref = Settings.getActivePreferences(document);

		boolean preserveBreakpoints = pref.getBoolean(Settings.PRESERVE_BREAKPOINTS, true);
		String codeStylePref = pref.get(Settings.GOOGLE_FORMATTER_CODE_STYLE, JavaFormatterOptions.Style.GOOGLE.name());

		//save with configured linefeed
		String lineFeed = Settings.getLineFeed(null, System.getProperty("line.separator"));
		document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
		document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);

		final String code;

		try {
			code = document.getText(0, document.getLength());
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
			return;
		}

		try {
			SortedSet<Pair<Integer, Integer>> regions = changedElements;
			if (CollectionUtils.isEmpty(changedElements)) {
				regions = new TreeSet<>();

				if (this.startOffset > -1 && this.endOffset > -1) {
					regions.add(Pair.of(this.startOffset, this.endOffset));
				} else {
					regions.add(Pair.of(0, code.length() - 1));
				}
			}

			GuardedSectionManager guards = GuardedSectionManager.getInstance(document);
			if (guards != null) {
				SortedSet<Pair<Integer, Integer>> nonGuardedSections = new TreeSet<>();
				Iterable<GuardedSection> guardedSections = guards.getGuardedSections();

				StringBuilder sb = new StringBuilder();
				guardedSections.forEach(guard -> sb.append(guard.getStartPosition().getOffset()).append("/").append(guard.getEndPosition().getOffset()).append(" "));
				log.log(Level.FINEST, "Guarded sections: {0}", sb.toString().trim());

				for (Pair<Integer, Integer> changedElement : regions) {
					nonGuardedSections.addAll(avoidGuardedSection(changedElement, guardedSections));
				}

				regions = nonGuardedSections;
			}

			final List<Pair<Integer, Integer>> regionsList = regions.stream().collect(Collectors.toList());

			StringBuilder sb = new StringBuilder();
			regionsList.stream().forEach(section -> sb.append(section.getLeft()).append("/").append(section.getRight()).append(" "));
			log.log(Level.FINEST, "Formating sections: {0}", sb.toString().trim());

			String formattedContent = formatter.format(code, JavaFormatterOptions.Style.valueOf(codeStylePref), regions);
			// quick check for changed
			if (setFormattedCode(code, formattedContent, guards, preserveBreakpoints)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Goolge formatter", Utils.iconGoogle, null, null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using Goolge formatter");
				});
			}
		} catch (RuntimeException ex) {
			throw ex;
		}
	}
}
