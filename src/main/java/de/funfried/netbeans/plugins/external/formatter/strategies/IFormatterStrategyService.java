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

import javax.swing.text.Document;

/**
 * Service interface for external formatter implementations.
 *
 * @author bahlef
 */
public interface IFormatterStrategyService extends IFormatterStrategy {
	/**
	 * Returns the continuation indent size configured for the given {@link Document},
	 * or {@code null} if it should not affect the editor behavior.
	 * 
	 * @param document the {@link Document} for which the continuation indent size
	 *                 is requested
	 *
	 * @return the continuation indent size configured for the given {@link Document},
	 *         or {@code null} if it should not affect the editor behavior
	 */
	Integer getContinuationIndentSize(Document document);

	/**
	 * Retruns the display name of this formatter implementation.
	 *
	 * @return the display name of this formatter implementation
	 */
	String getDisplayName();

	/**
	 * Retruns the unique identifier of this formatter implementation.
	 *
	 * @return the unique identifier of this formatter implementation
	 */
	String getId();

	/**
	 * Returns the indent size configured for the given {@link Document}, or
	 * {@code null} if it should not affect the editor behavior.
	 * 
	 * @param document the {@link Document} for which the indent size is requested
	 *
	 * @return the indent size configured for the given {@link Document}, or
	 *         {@code null} if it should not affect the editor behavior
	 */
	Integer getIndentSize(Document document);

	/**
	 * Returns the right margin (position of the red line in the editor) configured
	 * for the given {@link Document}, or {@code null} if it should not affect the
	 * editor behavior.
	 * 
	 * @param document the {@link Document} for which the right margin is requested
	 *
	 * @return the right margin (position of the red line in the editor) configured
	 *         for the given {@link Document}, or {@code null} if it should not
	 *         affect the editor behavior
	 */
	Integer getRightMargin(Document document);

	/**
	 * Returns the spaces per tab configured for the given {@link Document}, or
	 * {@code null} if it should not affect the editor behavior.
	 * 
	 * @param document the {@link Document} for which the spaces per tab is
	 *                 requested
	 *
	 * @return the spaces per tab configured for the given {@link Document}, or
	 *         {@code null} if it should not affect the editor behavior
	 */
	Integer getSpacesPerTab(Document document);

	/**
	 * Returns the expand tab to spaces flag configured for the given
	 * {@link Document}, or {@code null} if it should not affect the editor behavior.
	 * 
	 * @param document the {@link Document} for which the expand tab to spaces flag
	 *                 is requested
	 *
	 * @return the expand tab to spaces flag configured for the given
	 *         {@link Document}, or {@code null} if it should not affect the editor
	 *         behavior
	 */
	Boolean isExpandTabToSpaces(Document document);
}
