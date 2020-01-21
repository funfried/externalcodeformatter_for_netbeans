/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import java.io.IOException;
import java.io.StringReader;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.editor.document.LineDocumentUtils;
import org.netbeans.api.editor.guards.DocumentGuards;
import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.editor.BaseDocument;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

import de.funfried.netbeans.plugins.external.formatter.ui.editor.diff.Diff;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Abstract implementation of the formatter {@link Runnable}.
 *
 * @author bahlef
 */
public abstract class AbstractFormatterRunnable implements Runnable {
	private static final Logger log = Logger.getLogger(AbstractFormatterRunnable.class.getName());

	private static final Level logLevel = Level.FINER;

	/** {@link SortedSet} containing document offset ranges which should be formatted. */
	protected final SortedSet<Pair<Integer, Integer>> changedElements;

	/** The {@link StyledDocument} from which the content should be formatted. */
	protected final StyledDocument document;

	/**
	 * Constructor which has to be used by subclasses.
	 *
	 * @param document        the {@link StyledDocument} from which the content should be formatted
	 * @param changedElements {@link SortedSet} containing document offset ranges which should be formatted or {@code null} to format the whole document
	 */
	protected AbstractFormatterRunnable(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
		this.document = document;
		this.changedElements = changedElements;
	}

	/**
	 * Applies the given {@code formattedContent} to the {@code document}.
	 *
	 * @param code             the previous (unformatted) content
	 * @param formattedContent the formatted code
	 *
	 * @return {@code true} if and only if the given {@code formattedContent} was set to
	 *         the {@code document}, if due to any circumstances (old code equals formatted code,
	 *         thrown exceptions, ...) the {@code formattedContent} wasn't applied {@code false}
	 *         is returned
	 *
	 * @throws BadLocationException if there is an issue while applying the formatted code
	 */
	protected boolean setFormattedCode(String code, String formattedContent) throws BadLocationException {
		// quick check for changed
		if (formattedContent != null && /* does not support changes of EOL */ !formattedContent.equals(code)) {
			try (StringReader original = new StringReader(code);
					StringReader formatted = new StringReader(formattedContent)) {
				Difference[] differences = Diff.diff(original, formatted);
				if (differences != null) {
					for (Difference d : differences) {
						switch (d.getType()) {
							case Difference.ADD: {
								int startLine = d.getSecondStart();
								int start = NbDocument.findLineOffset(document, startLine - 1);

								String addText = d.getSecondText();

								document.insertString(start, addText, null);

								break;
							}
							case Difference.CHANGE: {
								int startLine = d.getSecondStart();
								int start = NbDocument.findLineOffset(document, startLine - 1);
								int length = d.getFirstText().length();

								String addText = d.getSecondText();

								document.remove(start, length);
								document.insertString(start, addText, null);

								break;
							}
							case Difference.DELETE: {
								int startLine = d.getSecondStart();
								int start = NbDocument.findLineOffset(document, startLine - 1);
								int length = d.getFirstText().length();

								document.remove(start, length);

								break;
							}
						}
					}
				}
			} catch (IOException ex) {
				log.log(Level.WARNING, "Could not create diff", ex);
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns the content of the {@code document} in respect to the given
	 * {@code lineFeedSetting}.
	 *
	 * @param lineFeedSetting the line feed setting
	 *
	 * @return The content of the {@code document} in respect to the given
	 *         {@code lineFeedSetting}
	 */
	protected String getCode(String lineFeedSetting) {
		//save with configured linefeed
		String lineFeed = Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
		if (null != lineFeedSetting) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);

			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Returns a {@link SortedSet} within ranges as {@link Pair}s of {@link Integer}s
	 * which describe the start and end offsets which can be formatted.
	 *
	 * @param code   the current unformatted content of the {@link document}
	 *
	 * @return A {@link SortedSet} within ranges as {@link Pair}s of {@link Integer}s
	 *         which describe the start and end offsets which can be formatted
	 */
	protected SortedSet<Pair<Integer, Integer>> getFormatableSections(String code) {
		SortedSet<Pair<Integer, Integer>> regions = changedElements;
		if (CollectionUtils.isEmpty(changedElements)) {
			regions = new TreeSet<>();

			regions.add(Pair.of(0, code.length() - 1));
		}

		DocumentGuards guards = LineDocumentUtils.as(document, DocumentGuards.class);
		if (guards != null) {
			if (log.isLoggable(logLevel)) {
				GuardedSectionManager guardMgmr = GuardedSectionManager.getInstance(document);
				if (guardMgmr != null) {
					Iterable<GuardedSection> guardedSections = guardMgmr.getGuardedSections();

					StringBuilder sb = new StringBuilder();
					guardedSections.forEach(guard -> sb.append(guard.getStartPosition().getOffset()).append("/").append(guard.getEndPosition().getOffset()).append(" "));
					log.log(logLevel, "Guarded sections: {0}", sb.toString().trim());
				}
			}

			SortedSet<Pair<Integer, Integer>> nonGuardedSections = new TreeSet<>();
			for (Pair<Integer, Integer> changedElement : regions) {
				int start = changedElement.getLeft();
				int end = changedElement.getRight();

				boolean startGuarded = guards.isPositionGuarded(start, true);
				boolean endGuarded = guards.isPositionGuarded(end, true);

				int startAdjusted = start;
				int endAdjusted = end;

				if (startGuarded && endGuarded) {
					startAdjusted = guards.adjustPosition(start, true);
					endAdjusted = guards.adjustPosition(end, false);

					if (startAdjusted >= endAdjusted) {
						continue;
					}
				} else if (startGuarded) {
					startAdjusted = guards.adjustPosition(start, true);
				} else if (endGuarded) {
					endAdjusted = guards.adjustPosition(end, false);
				}

				int nextGuard = guards.findNextBlock(startAdjusted, true);
				while (nextGuard != -1 && nextGuard < endAdjusted) {
					nonGuardedSections.add(Pair.of(startAdjusted, guards.adjustPosition(nextGuard, false)));

					startAdjusted = guards.adjustPosition(nextGuard, true);
					nextGuard = guards.findNextBlock(startAdjusted, true);
				}

				nonGuardedSections.add(Pair.of(startAdjusted, endAdjusted));
			}

			regions = nonGuardedSections;
		}

		if (log.isLoggable(logLevel)) {
			StringBuilder sb = new StringBuilder();
			regions.stream().forEach(section -> sb.append(section.getLeft()).append("/").append(section.getRight()).append(" "));
			log.log(logLevel, "Formating sections: {0}", sb.toString().trim());
		}

		return regions;
	}
}
