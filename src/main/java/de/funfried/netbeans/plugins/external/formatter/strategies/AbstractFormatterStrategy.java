/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import java.util.SortedSet;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.Utils;

/**
 *
 * @author bahlef
 */
public abstract class AbstractFormatterStrategy implements IFormatterStrategy {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format(FormatterAdvice fa) {
		final int selectionStart = fa.selectionStart;
		final int selectionEnd = fa.selectionEnd;
		final boolean forSave = fa.forSave;
		final SortedSet<Pair<Integer, Integer>> changedElements = fa.changedElements;
		final StyledDocument document = fa.styledDoc;
		final JTextComponent editor = fa.editor;
		final int caret = fa.caret;

		final int _dot = !forSave ? selectionStart : -1;
		final int _mark = !forSave ? selectionEnd : -1;
		final int _caret = caret;

		format(document, _dot, _mark, changedElements);

		if (editor != null) {
			SwingUtilities.invokeLater(() -> {
				//Set caret after the formatting, if possible
				if (_caret > 0) {
					final int car = Math.max(0, Math.min(_caret, editor.getDocument().getLength()));
					editor.setCaretPosition(car);
					editor.requestFocus();
					editor.requestFocusInWindow();
				}
			});
		}
	}

	protected abstract void format(StyledDocument document, int dot, int mark, SortedSet<Pair<Integer, Integer>> changedElements);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(StyledDocument document) {
		if (!Utils.isJava(document)) {
			return false;
		}

		return isActivated(document);
	}

	protected abstract boolean isActivated(StyledDocument document);
}
