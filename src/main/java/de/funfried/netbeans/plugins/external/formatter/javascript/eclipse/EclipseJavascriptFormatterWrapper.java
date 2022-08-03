/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.javascript.eclipse;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.jsdt.core.ToolFactory;
import org.eclipse.wst.jsdt.core.formatter.CodeFormatter;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;

/**
 * Wrapper class to the Eclipse formatter implementation.
 *
 * @author bahlef
 */
public final class EclipseJavascriptFormatterWrapper {
	/** Use to specify the kind of the code snippet to format. */
	private static final int FORMATTER_OPTS = CodeFormatter.K_JAVASCRIPT_UNIT;

	/**
	 * Package private Constructor for creating a new instance of {@link EclipseJavascriptFormatterWrapper}.
	 */
	EclipseJavascriptFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param formatterFile the path to the formatter configuration file
	 * @param formatterProfile the name of the formatter configuration profile
	 * @param code the unformatted code
	 * @param lineFeed the line feed to use for formatting
	 * @param changedElement an optional range as a {@link Pair} object defining the offsets which should be formatted
	 *
	 * @return the formatted code
	 *
	 * @throws ConfigReadException if there is an issue parsing the formatter configuration
	 * @throws ProfileNotFoundException if the given {@code profile} could not be found
	 * @throws CannotLoadConfigurationException if there is any issue accessing or reading the formatter configuration
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String formatterFile, String formatterProfile, String code, String lineFeed, Pair<Integer, Integer> changedElement)
			throws ConfigReadException, ProfileNotFoundException, CannotLoadConfigurationException, FormattingFailedException {
		if (code == null) {
			return null;
		}

		Map<String, String> allConfig = EclipseFormatterConfig.parseConfig(formatterFile, formatterProfile);

		CodeFormatter formatter = ToolFactory.createCodeFormatter(allConfig, ToolFactory.M_FORMAT_EXISTING);

		int codeLength = code.length();

		int offset = 0;
		int length = codeLength;

		if (changedElement != null && changedElement.getLeft() != null && changedElement.getRight() != null) {
			offset = changedElement.getLeft();
			if (offset < 0) {
				offset = 0;
			}

			length = (changedElement.getRight() - changedElement.getLeft()) + 1;
			if (length > codeLength) {
				length = codeLength;
			}
		}

		return format(formatter, code, offset, length, lineFeed);
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param formatter the {@link CodeFormatter}
	 * @param code the unformatted code
	 * @param offset the offset where to start formatting the given code
	 * @param length the length where to stop formatting the given code
	 * @param lineFeed the line feed to use for formatting
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	private String format(@NonNull CodeFormatter formatter, @NonNull String code, int offset, int length, String lineFeed) throws FormattingFailedException {
		String formattedCode = null;

		try {
			TextEdit te = formatter.format(FORMATTER_OPTS, code, offset, length, 0, lineFeed);
			if (te != null && te.getChildrenSize() > 0) {
				IDocument dc = new Document(code);
				te.apply(dc);

				formattedCode = dc.get();

				if (Objects.equals(code, formattedCode)) {
					return null;
				}
			}
		} catch (FormattingFailedException | IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new FormattingFailedException(ex);
		}

		return formattedCode;
	}
}
