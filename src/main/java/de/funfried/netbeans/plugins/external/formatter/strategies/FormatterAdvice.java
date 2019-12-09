/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import java.util.SortedSet;

import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class FormatterAdvice {
	private final StyledDocument styledDocument;

	private final SortedSet<Pair<Integer, Integer>> changedElements;

	private final boolean forSave;

	private final int selectionStart;

	private final int selectionEnd;

	private final int caret;

	private final JTextComponent editor;

	public FormatterAdvice(StyledDocument styledDocument, int selectionStart, int selectionEnd, int caret, JTextComponent editor) {
		this(styledDocument, null, false, selectionStart, selectionEnd, caret, editor);
	}

	public FormatterAdvice(StyledDocument styledDocument, SortedSet<Pair<Integer, Integer>> changedElements, int caret, JTextComponent editor) {
		this(styledDocument, changedElements, true, -1, -1, caret, editor);
	}

	private FormatterAdvice(StyledDocument styledDocument, SortedSet<Pair<Integer, Integer>> changedElements, boolean forSave, int selectionStart, int selectionEnd, int caret, JTextComponent editor) {
		this.styledDocument = styledDocument;
		this.changedElements = changedElements;
		this.forSave = forSave;
		this.selectionStart = selectionStart;
		this.selectionEnd = selectionEnd;
		this.caret = caret;
		this.editor = editor;
	}

	public StyledDocument getStyledDocument() {
		return styledDocument;
	}

	public SortedSet<Pair<Integer, Integer>> getChangedElements() {
		return changedElements;
	}

	public boolean isForSave() {
		return forSave;
	}

	public int getSelectionStart() {
		return selectionStart;
	}

	public int getSelectionEnd() {
		return selectionEnd;
	}

	public int getCaret() {
		return caret;
	}

	public JTextComponent getEditor() {
		return editor;
	}
}
