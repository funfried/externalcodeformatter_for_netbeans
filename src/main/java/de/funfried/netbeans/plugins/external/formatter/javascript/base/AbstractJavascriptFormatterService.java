/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.javascript.base;

import java.util.SortedSet;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;

/**
 * Abstract base implementation of {@link FormatterService} for javascript formatters.
 *
 * @author bahlef
 */
public abstract class AbstractJavascriptFormatterService implements FormatterService {
	/**
	 * Returns the {@link FormatJob}.
	 *
	 * @param document the {@link StyledDocument} which should be formatted
	 */
	protected abstract FormatJob getFormatJob(StyledDocument document);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) throws BadLocationException {
		getFormatJob(document).format();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSupportedMimeType() {
		return "text/javascript";
	}
}
