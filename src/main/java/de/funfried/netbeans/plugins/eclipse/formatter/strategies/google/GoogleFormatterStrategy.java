/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.eclipse.formatter.strategies.google;

import de.funfried.netbeans.plugins.eclipse.formatter.strategies.ParameterObject;
import de.funfried.netbeans.plugins.eclipse.formatter.strategies.IFormatterStrategy;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.eclipse.formatter.Utils;
import de.funfried.netbeans.plugins.eclipse.formatter.options.Settings;

/**
 *
 * @author markiewb
 */
public class GoogleFormatterStrategy implements IFormatterStrategy {
	private final GoogleFormatter formatter = new GoogleFormatter();

	/**
	 * @param po the {@link ParameterObject}
	 */
	@Override
	public void format(ParameterObject po) {
		final int selectionStart = po.selectionStart;
		final int selectionEnd = po.selectionEnd;
		final boolean forSave = po.forSave;
		final SortedSet<Pair<Integer, Integer>> changedElements = po.changedElements;
		final StyledDocument document = po.styledDoc;
		final JTextComponent editor = po.editor;
		final int caret = po.caret;

		final int _dot = (!forSave) ? selectionStart : -1;
		final int _mark = (!forSave) ? selectionEnd : -1;
		final int _caret = caret;

		final GoogleFormatterRunnable formatterRunnable = new GoogleFormatterRunnable(document, formatter, _dot, _mark, changedElements);
		formatterRunnable.run();

		SwingUtilities.invokeLater(() -> {
			//Set caret after the formatting, if possible
			if (editor != null && _caret > 0) {
				final int car = Math.max(0, Math.min(_caret, editor.getDocument().getLength()));
				editor.setCaretPosition(car);
				editor.requestFocus();
				editor.requestFocusInWindow();
			}
		});
	}

	@Override
	public boolean canHandle(StyledDocument document) {
		if (!Utils.isJava(document)) {
			return false;
		}

		Preferences pref = Settings.getActivePreferences(document);
		return pref.getBoolean(Settings.GOOGLE_FORMATTER_ENABLED, false);
	}
}
