/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.eclipse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.TextEdit;
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
public final class EclipseJavaFormatterWrapper {
	/** Use to specify the kind of the code snippet to format. */
	private static final int FORMATTER_OPTS = CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS /* + CodeFormatter.K_CLASS_BODY_DECLARATIONS + CodeFormatter.K_STATEMENTS */;

	/**
	 * Package private Constructor for creating a new instance of {@link EclipseJavaFormatterWrapper}.
	 */
	EclipseJavaFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param formatterFile    the path to the formatter configuration file
	 * @param formatterProfile the name of the formatter configuration profile
	 * @param code             the unformatted code
	 * @param lineFeed         the line feed to use for formatting
	 * @param sourceLevel      the source level to use for formatting
	 * @param changedElements  a {@link SortedSet} containing ranges as {@link Pair} objects defining the offsets which should be formatted
	 *
	 * @return the formatted code
	 *
	 * @throws ConfigReadException              if there is an issue parsing the formatter configuration
	 * @throws ProfileNotFoundException         if the given {@code profile} could not be found
	 * @throws CannotLoadConfigurationException if there is any issue accessing or reading the formatter configuration
	 * @throws FormattingFailedException        if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String formatterFile, String formatterProfile, String code, String lineFeed, String sourceLevel, SortedSet<Pair<Integer, Integer>> changedElements)
			throws ConfigReadException, ProfileNotFoundException, CannotLoadConfigurationException, FormattingFailedException {
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

		Map<String, String> allConfig = EclipseFormatterConfig.parseConfig(formatterFile, formatterProfile, sourceLevel);

		CodeFormatter formatter = ToolFactory.createCodeFormatter(allConfig, ToolFactory.M_FORMAT_EXISTING);
		//see http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fcore%2Fformatter%2FCodeFormatter.html&anchor=format(int,

		return format(formatter, code, regions.toArray(new IRegion[regions.size()]), lineFeed);
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param formatter the {@link CodeFormatter}
	 * @param code      the unformatted code
	 * @param lineFeed  the line feed to use for formatting
	 * @param regions   an array containing {@link IRegion} objects defining the offsets which should be formatted
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	private String format(@NonNull CodeFormatter formatter, @NonNull String code, @NonNull IRegion[] regions, String lineFeed) throws FormattingFailedException {
		String formattedCode = null;

		TextEdit te = formatter.format(FORMATTER_OPTS, code, regions, 0, lineFeed);
		if (te != null && te.getChildrenSize() > 0) {
			try {
				IDocument dc = new Document(code);
				te.apply(dc);

				formattedCode = dc.get();

				if (Objects.equals(code, formattedCode)) {
					return null;
				}
			} catch (Exception ex) {
				throw new FormattingFailedException("Failed to format the given code.", ex);
			}
		} else {
			throw new FormattingFailedException("Formatting the given code ended in a null result.");
		}

		return formattedCode;
	}
}
