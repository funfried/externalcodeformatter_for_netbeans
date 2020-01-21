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

	/**
	 * Creates a new {@link FormatterAdvice}.
	 *
	 * @param styledDocument  the {@link StyledDocument} which should be formatted
	 * @param changedElements a {@link SortedSet} of {@link Pair} objects as ranges which should be formatted, leaving everything outside of the ranges unformatted
	 */
	public FormatterAdvice(StyledDocument styledDocument, SortedSet<Pair<Integer, Integer>> changedElements) {
		this.styledDocument = styledDocument;
		this.changedElements = changedElements;
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
}
