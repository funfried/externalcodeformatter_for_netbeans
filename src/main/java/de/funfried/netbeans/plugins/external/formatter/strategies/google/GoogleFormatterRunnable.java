/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.google;

import java.util.SortedSet;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import com.google.googlejavaformat.java.JavaFormatterOptions;

import de.funfried.netbeans.plugins.external.formatter.Utils;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractFormatterRunnable;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

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

		String code = getCode(null);

		try {
			GuardedSectionManager guards = GuardedSectionManager.getInstance(document);
			SortedSet<Pair<Integer, Integer>> regions = getFormatableSections(code, guards);

			String formattedContent = formatter.format(code, JavaFormatterOptions.Style.valueOf(codeStylePref), regions);
			// quick check for changed
			if (setFormattedCode(code, formattedContent, preserveBreakpoints)) {
				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Goolge formatter", Icons.ICON_GOOGLE, null, null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using Goolge formatter");
				});
			}
		} catch (RuntimeException ex) {
			throw ex;
		}
	}
}
