/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import java.util.SortedSet;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.Utils;

/**
 * Abstract base implementation of {@link AbstractFormatterRunnable} for Java formatters.
 *
 * @author bahlef
 */
public abstract class AbstractJavaFormatterStrategy implements IFormatterStrategyService {
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

	/**
	 * Returns the runnable which implements the format logic.
	 *
	 * @param document        the {@link StyledDocument} which should be formatted
	 * @param changedElements a {@link SortedSet} containing ranges as {@link Pair} objects that should be formatted
	 */
	protected abstract Runnable getRunnable(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format(FormatterAdvice fa) {
		getRunnable(fa.getStyledDocument(), fa.getChangedElements()).run();
	}
}
