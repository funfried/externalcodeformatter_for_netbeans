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
 * Pojo to keep formatter specific values together.
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

	/**
	 * Creates a new {@link FormatterAdvice}.
	 *
	 * @param styledDocument the {@link StyledDocument} which should be formatted
	 * @param selectionStart offset where the selection starts, or {@code -1} if there is no selection
	 * @param selectionEnd   offset where the selection ends, or {@code -1} if there is no selection
	 * @param caret          the caret position when the formatting was invoked
	 * @param editor         the {@link JTextComponent} holding the given {@link StyledDocument}
	 */
	public FormatterAdvice(StyledDocument styledDocument, int selectionStart, int selectionEnd, int caret, JTextComponent editor) {
		this(styledDocument, null, false, selectionStart, selectionEnd, caret, editor);
	}

	/**
	 * Creates a new {@link FormatterAdvice}.
	 *
	 * @param styledDocument  the {@link StyledDocument} which should be formatted
	 * @param changedElements a {@link SortedSet} of {@link Pair} objects as ranges which should be formatted, leaving everything outside of the ranges unformatted
	 * @param caret           the caret position when the formatting was invoked
	 * @param editor          the {@link JTextComponent} holding the given {@link StyledDocument}
	 */
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

	/**
	 * Returns the {@link StyledDocument}.
	 *
	 * @return the {@link StyledDocument}
	 */
	public StyledDocument getStyledDocument() {
		return styledDocument;
	}

	/**
	 * Returns the {@link SortedSet} of {@link Pair} ranges.
	 *
	 * @return the {@link SortedSet} of {@link Pair} ranges
	 */
	public SortedSet<Pair<Integer, Integer>> getChangedElements() {
		return changedElements;
	}

	/**
	 * Return the {@code forSave} flag.
	 *
	 * @return the {@code forSave} flag
	 */
	public boolean isForSave() {
		return forSave;
	}

	/**
	 * Returns the offset of the selection start.
	 *
	 * @return the offset of the selection start
	 */
	public int getSelectionStart() {
		return selectionStart;
	}

	/**
	 * Returns the offset of the selection end.
	 *
	 * @return the offset of the selection end
	 */
	public int getSelectionEnd() {
		return selectionEnd;
	}

	/**
	 * Returns the caret position.
	 *
	 * @return the caret position
	 */
	public int getCaret() {
		return caret;
	}

	/**
	 * Returns the {@link JTextComponent}.
	 *
	 * @return the {@link JTextComponent}
	 */
	public JTextComponent getEditor() {
		return editor;
	}
}
