/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter.ui.editor;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.indent.spi.Context;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

/**
 *
 * @author bahlef
 */
public class EditorUtils {
	/**
	 * Private constructor due to static methods only.
	 */
	private EditorUtils() {
	}

	/**
	 * Returns the {@link Context#indentRegions()} from the given {@link Context} as a
	 * {@link SortedSet}.
	 *
	 * @param context the {@link Context}
	 *
	 * @return the {@link Context#indentRegions()} from the given {@link Context} as a
	 *         {@link SortedSet}
	 */
	@NonNull
	public static SortedSet<Pair<Integer, Integer>> getChangedElements(Context context) {
		SortedSet<Pair<Integer, Integer>> changedElements = new TreeSet<>();

		if (context != null) {
			Document document = context.document();
			if (document != null) {
				int documentLength = document.getLength();

				List<Context.Region> regions = context.indentRegions();
				for (Context.Region region : regions) {
					int start = region.getStartOffset();
					if (start < 0) {
						continue;
					}

					int end = region.getEndOffset();
					if (end >= documentLength) {
						end = documentLength - 1;
					}

					changedElements.add(Pair.of(start, end));
				}
			}
		}

		return changedElements;
	}

	/**
	 * Returns the given {@link Document} as a {@link StyledDocument} or {@code null} if
	 * the given {@link Document} isn't a {@link StyledDocument}.
	 *
	 * @param document the {@link Document}
	 *
	 * @return the given {@link Document} as a {@link StyledDocument} or {@code null} if
	 *         the given {@link Document} isn't a {@link StyledDocument}
	 */
	@CheckForNull
	public static StyledDocument toStyledDocument(Document document) {
		StyledDocument styledDocument = null;
		if (document != null) {
			if (document instanceof StyledDocument) {
				styledDocument = (StyledDocument) document;
			} else {
				DataObject dataObject = NbEditorUtilities.getDataObject(document);
				if (dataObject != null) {
					styledDocument = NbDocument.getDocument(dataObject);
				}
			}
		}

		return styledDocument;
	}
}
