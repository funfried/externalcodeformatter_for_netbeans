/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter;

import java.util.List;
import java.util.SortedSet;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;

/**
 * Service interface for external formatter implementations.
 *
 * @author bahlef
 */
public interface FormatterService {
	/**
	 * Returns {@code true} if and only if this implementation would be able to
	 * format the given {@link Document}, otherwise {@code false}.
	 *
	 * @param document the {@link Document} to check
	 *
	 * @return {@code true} if and only if this implementation would be able to
	 *         format the given {@link Document}, otherwise {@code false}
	 */
	default boolean canHandle(Document document) {
		if (document == null) {
			return false;
		}

		return MimeType.canHandle(getSupportedMimeTypes(), MimeType.getMimeTypeAsString(document));
	}

	/**
	 * Formats the given {@link StyledDocument} in regard to the given {@code changedElements}.
	 *
	 * @param document the {@link StyledDocument} which should be formatted
	 * @param changedElements a {@link SortedSet} containing ranges as {@link Pair} objects that should be formatted
	 *
	 * @return if {@code true} formatting was done, otherwise formatting was rejected and needs to be done by NetBeans internal formatter
	 *
	 * @throws BadLocationException if something goes wrong while applying the formatted code
	 * @throws FormattingFailedException if the given {@link StyledDocument} cannot be formatted by the given formatter
	 */
	boolean format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) throws BadLocationException, FormattingFailedException;

	/**
	 * Returns the continuation indent size configured for the given {@link Document},
	 * or {@code null} if it should not affect the editor behavior.
	 *
	 * @param document the {@link Document} for which the continuation indent size
	 *        is requested
	 *
	 * @return the continuation indent size configured for the given {@link Document},
	 *         or {@code null} if it should not affect the editor behavior
	 */
	@CheckForNull
	Integer getContinuationIndentSize(Document document);

	/**
	 * Retruns the display name of this formatter implementation.
	 *
	 * @return the display name of this formatter implementation
	 */
	@NonNull
	String getDisplayName();

	/**
	 * Retruns the unique identifier of this formatter implementation.
	 *
	 * @return the unique identifier of this formatter implementation
	 */
	@NonNull
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
	@CheckForNull
	Integer getIndentSize(Document document);

	/**
	 * Creates and returns the {@link FormatterOptionsPanel} for this formatter
	 * which will be displayed in the overall options dialog underneath this
	 * formatters selection.
	 *
	 * @param project the {@link Project} if the panel which is created is used
	 *        to modify project specific settings, otherwise
	 *        {@code null}
	 *
	 * @return the {@link FormatterOptionsPanel} for this formatter, or
	 *         {@code null} if there are no options a user could make for
	 *         this formatter
	 */
	FormatterOptionsPanel createOptionsPanel(Project project);

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
	@CheckForNull
	Integer getRightMargin(Document document);

	/**
	 * Returns the spaces per tab configured for the given {@link Document}, or
	 * {@code null} if it should not affect the editor behavior.
	 *
	 * @param document the {@link Document} for which the spaces per tab is
	 *        requested
	 *
	 * @return the spaces per tab configured for the given {@link Document}, or
	 *         {@code null} if it should not affect the editor behavior
	 */
	@CheckForNull
	Integer getSpacesPerTab(Document document);

	/**
	 * Returns a {@link List} of supported {@link MimeType}s for this {@link FormatterService}.
	 *
	 * @return a {@link List} of supported {@link MimeType}s for this {@link FormatterService}
	 */
	@NonNull
	List<MimeType> getSupportedMimeTypes();

	/**
	 * Returns the expand tab to spaces flag configured for the given
	 * {@link Document}, or {@code null} if it should not affect the editor behavior.
	 *
	 * @param document the {@link Document} for which the expand tab to spaces flag
	 *        is requested
	 *
	 * @return the expand tab to spaces flag configured for the given
	 *         {@link Document}, or {@code null} if it should not affect the editor
	 *         behavior
	 */
	@CheckForNull
	Boolean isExpandTabToSpaces(Document document);

	/**
	 * Organizes the imports of the given {@link StyledDocument}.
	 *
	 * @param document the {@link StyledDocument}
	 * @param afterFixImports {@code true} if this method was called after fixing imports,
	 *        otherwise {@code false}
	 *
	 * @return {@code true} if the imports have been reorganized, if something went wrong
	 *         it will return {@code false}, if it wasn't executed, e.g. because it is not
	 *         activated through its configuration, it will return {@code null}
	 *
	 * @throws BadLocationException if something goes wrong while applying the reorganized imports code
	 */
	@CheckForNull
	Boolean organizeImports(StyledDocument document, boolean afterFixImports) throws BadLocationException;
}
