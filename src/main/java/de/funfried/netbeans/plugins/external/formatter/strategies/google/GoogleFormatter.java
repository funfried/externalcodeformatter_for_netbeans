/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.google;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Range;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;

/**
 *
 * @author bahlef
 */
public final class GoogleFormatter {
	GoogleFormatter() {
	}

	public String format(String code, JavaFormatterOptions.Style codeStyle, SortedSet<Pair<Integer, Integer>> changedElements) {
		if (code == null) {
			return null;
		}

		if (codeStyle == null) {
			codeStyle = JavaFormatterOptions.Style.GOOGLE;
		}

		Collection<Range<Integer>> characterRanges = new ArrayList<>();
		if (!CollectionUtils.isEmpty(changedElements)) {
			for (Pair<Integer, Integer> changedElement : changedElements) {
				characterRanges.add(Range.closed(changedElement.getLeft(), changedElement.getRight()));
			}
		} else {
			characterRanges.add(Range.closed(0, code.length() - 1));
		}

		try {
			Formatter formatter = new Formatter(JavaFormatterOptions.builder().style(codeStyle).build());
			return formatter.formatSource(code, characterRanges);
		} catch (FormatterException ex) {
			throw new RuntimeException(ex);
		}
	}
}
