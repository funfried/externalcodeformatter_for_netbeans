/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.json.base;

import java.util.SortedSet;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Abstract base implementation of {@link FormatterService} for Json formatters.
 *
 * @author bahlef
 */
public abstract class AbstractJsonFormatterService implements FormatterService {
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
	public void format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) throws BadLocationException, FormattingFailedException {
		if (!canHandle(document)) {
			throw new FormattingFailedException("The file type '" + MimeType.getMimeTypeAsString(document) + "' is not supported");
		}

		getFormatJob(document).format();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MimeType getSupportedMimeType() {
		return MimeType.JSON;
	}
}
