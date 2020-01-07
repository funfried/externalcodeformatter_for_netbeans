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
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.Utils;

/**
 *
 * @author bahlef
 */
public abstract class AbstractJavaFormatterStrategy implements IFormatterStrategyService {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format(FormatterAdvice fa) {
		int _dot = !fa.isForSave() ? fa.getSelectionStart() : -1;
		int _mark = !fa.isForSave() ? fa.getSelectionEnd() : -1;

		format(fa.getStyledDocument(), _dot, _mark, fa.getChangedElements());

		JTextComponent editor = fa.getEditor();
		if (editor != null) {
			SwingUtilities.invokeLater(() -> {
				//Set caret after the formatting, if possible
				if (fa.getCaret() > 0) {
					final int car = Math.max(0, Math.min(fa.getCaret(), editor.getDocument().getLength()));
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
	public boolean canHandle(Document document) {
		if (!Utils.isJava(document)) {
			return false;
		}

		return true;
	}
}
