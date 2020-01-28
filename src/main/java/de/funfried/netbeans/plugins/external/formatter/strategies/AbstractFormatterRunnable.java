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

import java.io.IOException;
import java.io.StringReader;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.editor.BaseDocument;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

import de.funfried.netbeans.plugins.external.formatter.ui.editor.diff.Diff;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Abstract implementation of the formatter {@link Runnable} which is called by the
 * {@link IFormatterStrategy} where this implementation belongs to.
 *
 * @author bahlef
 */
public abstract class AbstractFormatterRunnable implements Runnable {
	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(AbstractFormatterRunnable.class.getName());

	/** Log {@link Level} for fast switching while investigating issues. */
	private static final Level logLevel = Level.WARNING;

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
				if (differences != null && differences.length != 0) {
					if (log.isLoggable(logLevel)) {
						log.log(logLevel, "Unformatted: ''{0}''", code);
						log.log(logLevel, "Formatted: ''{0}''", formattedContent);
					}

					for (Difference d : differences) {
						int startLine = d.getSecondStart();

						switch (d.getType()) {
							case Difference.ADD: {
								int start = NbDocument.findLineOffset(document, startLine - 1);
								String addText = d.getSecondText();

								if (log.isLoggable(logLevel)) {
									log.log(logLevel, "ADD: {0} / Line {1}: {2}", new Object[] { start, startLine, addText });
								}

								document.insertString(start, addText, null);

								break;
							}
							case Difference.CHANGE: {
								int start = NbDocument.findLineOffset(document, startLine - 1);
								String removeText = d.getFirstText();
								int length = removeText.length();

								String addText = d.getSecondText();

								if (log.isLoggable(logLevel)) {
									log.log(logLevel, "CHANGE: {0} - {1} / Line {2}: ''{3}'' => ''{4}'' ({5})", new Object[] { start, length, startLine, addText, document.getText(start, length), removeText });
								}

								document.remove(start, length);
								document.insertString(start, addText, null);

								break;
							}
							case Difference.DELETE: {
								int start = NbDocument.findLineOffset(document, startLine);
								String removeText = d.getFirstText();
								int length = removeText.length();

								if (log.isLoggable(logLevel)) {
									log.log(logLevel, "DELETE: {0} - {1} / Line {2}: ''{3}'' ({4})", new Object[] { start, length, startLine, document.getText(start, length), removeText });
								}

								document.remove(start, length);

								break;
							}
						}
					}

					return true;
				}
			} catch (IOException ex) {
				log.log(Level.WARNING, "Could not create diff", ex);
			}
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
	 * which describe the start and end offsets that can be formatted, it automatically
	 * checks for guarded sections and removes them before returning the {@link SortedSet},
	 * this means if an empty {@link SortedSet} was removed nothing can be formatted,
	 * because all ranges in the {@code changedElements} are in guarded sections.
	 *
	 * @param code the current unformatted content of the {@link document}
	 *
	 * @return A {@link SortedSet} within ranges as {@link Pair}s of {@link Integer}s
	 *         which describe the start and end offsets which can be formatted or an empty
	 *         {@link SortedSet} if nothing of the {@code changedElements} can be formatted
	 */
	@NonNull
	protected SortedSet<Pair<Integer, Integer>> getFormatableSections(String code) {
		SortedSet<Pair<Integer, Integer>> regions = changedElements;
		if (CollectionUtils.isEmpty(changedElements)) {
			regions = new TreeSet<>();

			regions.add(Pair.of(0, code.length() - 1));
		}

		GuardedSectionManager guards = GuardedSectionManager.getInstance(document);
		if (guards != null) {
			SortedSet<Pair<Integer, Integer>> nonGuardedSections = new TreeSet<>();
			Iterable<GuardedSection> guardedSections = guards.getGuardedSections();

			if (log.isLoggable(logLevel)) {
				{
					StringBuilder sb = new StringBuilder();
					regions.stream().forEach(section -> sb.append(section.getLeft()).append("/").append(section.getRight()).append(" "));
					log.log(logLevel, "Formating sections before guards: {0}", sb.toString().trim());
				}

				{
					StringBuilder sb = new StringBuilder();
					guardedSections.forEach(guard -> sb.append(guard.getStartPosition().getOffset()).append("/").append(guard.getEndPosition().getOffset()).append(" "));
					log.log(logLevel, "Guarded sections: {0}", sb.toString().trim());
				}
			}

			for (Pair<Integer, Integer> changedElement : regions) {
				nonGuardedSections.addAll(avoidGuardedSection(changedElement, guardedSections));
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

	/**
	 * Checks if a given {@code section} interferes with the given {@code guardedSections}
	 * and if so splits the given {@code section} into multiple sections and returns them
	 * as a {@link SortedSet}.
	 *
	 * @param section         the section that should be checked
	 * @param guardedSections the guarded sections of the {@code document}
	 *
	 * @return A {@link SortedSet} containing the splitted sections or just the initial
	 *         {@code section} itself if there was no interference with the given
	 *         {@code guardedSections}
	 */
	protected SortedSet<Pair<Integer, Integer>> avoidGuardedSection(Pair<Integer, Integer> section, Iterable<GuardedSection> guardedSections) {
		SortedSet<Pair<Integer, Integer>> ret = new TreeSet<>();

		MutableInt start = new MutableInt(section.getLeft());
		MutableInt end = new MutableInt(section.getRight());

		if (guardedSections != null) {
			try {
				guardedSections.forEach(guardedSection -> {
					if (start.getValue() >= guardedSection.getStartPosition().getOffset() && start.getValue() <= guardedSection.getEndPosition().getOffset()) {
						if (end.getValue() > guardedSection.getEndPosition().getOffset()) {
							start.setValue(guardedSection.getEndPosition().getOffset());
						} else {
							start.setValue(-1);
							end.setValue(-1);

							throw new BreakException();
						}
					} else if (end.getValue() >= guardedSection.getStartPosition().getOffset() && end.getValue() <= guardedSection.getEndPosition().getOffset()) {
						if (start.getValue() < guardedSection.getStartPosition().getOffset()) {
							end.setValue(guardedSection.getStartPosition().getOffset() - 1);
						} else {
							start.setValue(-1);
							end.setValue(-1);

							throw new BreakException();
						}
					} else if (start.getValue() < guardedSection.getStartPosition().getOffset() && end.getValue() > guardedSection.getEndPosition().getOffset()) {
						ret.add(Pair.of(start.getValue(), guardedSection.getStartPosition().getOffset() - 1));

						start.setValue(guardedSection.getEndPosition().getOffset());
					}
				});
			} catch (BreakException ex) {
				// found no better solution to break a forEach
			}
		}

		if (start.getValue() > -1 && end.getValue() > -1) {
			ret.add(Pair.of(start.getValue(), end.getValue()));
		}

		return ret;
	}

	/**
	 * {@link RuntimeException} which is used as a {@code break} condition inside
	 * a {@link Iterable#forEach(java.util.function.Consumer)}.
	 */
	protected static class BreakException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
