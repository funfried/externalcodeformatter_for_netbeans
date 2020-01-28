/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.base.java;

import java.util.SortedSet;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.Utils;
import de.funfried.netbeans.plugins.external.formatter.base.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.base.FormatterService;

/**
 * Abstract base implementation of {@link FormatterService} for Java formatters.
 *
 * @author bahlef
 */
public abstract class AbstractJavaFormatterService implements FormatterService {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(Document document) {
		if (Utils.isJava(document)) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the {@link FormatJob}.
	 *
	 * @param document        the {@link StyledDocument} which should be formatted
	 * @param changedElements a {@link SortedSet} containing ranges as {@link Pair} objects that should be formatted
	 */
	protected abstract FormatJob getFormatJob(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) throws BadLocationException {
		getFormatJob(document, changedElements).format();
	}
}
