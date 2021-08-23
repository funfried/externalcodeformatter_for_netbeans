/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.TextEdit;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import io.spring.javaformat.formatter.Formatter;

/**
 * Delegation class to the Spring formatter implementation.
 *
 * @author bahlef
 */
public final class SpringJavaFormatterWrapper {
	/** Use to specify the kind of the code snippet to format. */
	private static final int FORMATTER_OPTS = CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS /* + CodeFormatter.K_CLASS_BODY_DECLARATIONS + CodeFormatter.K_STATEMENTS */;

	/** The Spring {@link Formatter}. */
	private final Formatter formatter = new Formatter();

	/**
	 * Package private Constructor for creating a new instance of {@link SpringJavaFormatterWrapper}.
	 */
	SpringJavaFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted code
	 * @param lineFeed the line feed to use for formatting
	 * @param changedElements a {@link SortedSet} containing ranges as {@link Pair}
	 *        objects defining the offsets which should be formatted
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, String lineFeed, SortedSet<Pair<Integer, Integer>> changedElements) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		int codeLength = code.length();

		List<IRegion> regions = new ArrayList<>();
		if (changedElements == null) {
			regions.add(new Region(0, codeLength));
		} else if (!CollectionUtils.isEmpty(changedElements)) {
			for (Pair<Integer, Integer> changedElement : changedElements) {
				int length = (changedElement.getRight() - changedElement.getLeft()) + 1;
				if (length > codeLength) {
					length = codeLength;
				}

				regions.add(new Region(changedElement.getLeft(), length));
			}
		} else {
			// empty changed elements means nothing's left which can be formatted due to guarded sections
			return code;
		}

		return format(code, regions.toArray(new IRegion[regions.size()]), lineFeed);
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param formatter the {@link CodeFormatter}
	 * @param code the unformatted code
	 * @param lineFeed the line feed to use for formatting
	 * @param regions an array containing {@link IRegion} objects defining the offsets which should be formatted
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	private String format(@NonNull String code, @NonNull IRegion[] regions, String lineFeed) throws FormattingFailedException {
		String formattedCode = null;

		try {
			TextEdit te = formatter.format(FORMATTER_OPTS, code, regions, 0, lineFeed);
			//TextEdit te = formatter.format(code, regions, lineFeed);
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
			Logger.getLogger(SpringJavaFormatterWrapper.class.getName()).log(Level.SEVERE, "Formatting ran into", ex);

			throw new FormattingFailedException(ex);
		}

		return formattedCode;
	}
}
