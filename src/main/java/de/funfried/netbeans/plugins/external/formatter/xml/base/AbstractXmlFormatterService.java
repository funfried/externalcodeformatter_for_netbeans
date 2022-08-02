/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.xml.base;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Abstract base implementation of {@link FormatterService} for XML formatters.
 *
 * @author bahlef
 */
public abstract class AbstractXmlFormatterService implements FormatterService {
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
	public boolean format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) throws BadLocationException, FormattingFailedException {
		if (!canHandle(document)) {
			throw new FormattingFailedException("The file type '" + MimeType.getMimeTypeAsString(document) + "' is not supported");
		}

		getFormatJob(document).format();

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MimeType> getSupportedMimeTypes() {
		return Collections.singletonList(MimeType.XML);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean organizeImports(StyledDocument document, boolean afterFixImports) throws BadLocationException {
		return null;
	}
}
