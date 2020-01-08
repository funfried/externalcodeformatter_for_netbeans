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

import javax.swing.text.Document;

/**
 * Base interface for formatter implementations.
 *
 * @author markiewb
 * @author bahlef
 */
public interface IFormatterStrategy {
	/**
	 * Returns {@code true} if and only if this implementation would be able to
	 * format the given {@link Document}, otherwise {@code false}.
	 *
	 * @param document the {@link Document} to check
	 *
	 * @return {@code true} if and only if this implementation would be able to
	 *         format the given {@link Document}, otherwise {@code false}
	 */
	boolean canHandle(Document document);

	/**
	 * Formats the {@link StyledDocument} given in the {@link FormatterAdvice} in
	 * regard to the values included in that {@link FormatterAdvice}.
	 *
	 * @param fa the {@link FormatterAdvice} containing detailed instruction on
	 *           what to format
	 */
	void format(FormatterAdvice fa);
}
