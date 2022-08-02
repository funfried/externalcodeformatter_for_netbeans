/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.palantir;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;

import com.google.common.collect.Range;
import com.palantir.javaformat.java.Formatter;
import com.palantir.javaformat.java.FormatterException;
import com.palantir.javaformat.java.ImportOrderer;
import com.palantir.javaformat.java.JavaFormatterOptions;
import com.palantir.javaformat.java.RemoveUnusedImports;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Delegation class to the Palantir formatter implementation.
 *
 * @author bahlef
 */
public final class PalantirJavaFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link PalantirJavaFormatterWrapper}.
	 */
	PalantirJavaFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted code
	 * @param changedElements a {@link SortedSet} containing ranges as {@link Pair}
	 *        objects defining the offsets which should be formatted
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, SortedSet<Pair<Integer, Integer>> changedElements) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		Collection<Range<Integer>> characterRanges = new ArrayList<>();
		if (changedElements == null) {
			characterRanges.add(Range.closedOpen(0, code.length()));
		} else if (!CollectionUtils.isEmpty(changedElements)) { // empty changed elements means nothing's left which can be formatted due to guarded sections
			for (Pair<Integer, Integer> changedElement : changedElements) {
				int start = changedElement.getLeft();
				int end = changedElement.getRight();

				if (start == end) {
					end++;
				}

				characterRanges.add(Range.open(start, end));
			}
		}

		if (changedElements == null || !CollectionUtils.isEmpty(changedElements)) {
			try {
				Formatter formatter = Formatter.createFormatter(JavaFormatterOptions.builder().style(JavaFormatterOptions.Style.PALANTIR).build());
				code = formatter.formatSource(code, characterRanges);
			} catch (FormatterException ex) {
				throw new FormattingFailedException(ex);
			}
		}

		return code;
	}

	@CheckForNull
	public String organizeImports(String code) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		try {
			code = RemoveUnusedImports.removeUnusedImports(code);
		} catch (FormatterException ex) {
			throw new FormattingFailedException(ex);
		}

		try {
			code = ImportOrderer.reorderImports(code, JavaFormatterOptions.Style.PALANTIR);
		} catch (FormatterException ex) {
			throw new FormattingFailedException(ex);
		}

		return code;
	}
}
