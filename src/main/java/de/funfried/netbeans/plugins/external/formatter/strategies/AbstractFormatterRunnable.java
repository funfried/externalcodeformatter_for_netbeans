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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.ClassLoadUnloadBreakpoint;
import org.netbeans.api.debugger.jpda.FieldBreakpoint;
import org.netbeans.api.debugger.jpda.LineBreakpoint;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.maven.classpath.MavenSourcesImpl;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Abstract implementation of the formatter {@link Runnable}. {@link LineBreakpoint}s get
 * removed and the following breakpoints are getting reattached:
 * <ul>
 * <li>{@link ClassLoadUnloadBreakpoint}</li>
 * <li>{@link FieldBreakpoint}</li>
 * <li>{@link MethodBreakpoint}</li>
 * </ul>
 *
 * @author bahlef
 */
public abstract class AbstractFormatterRunnable implements Runnable {
	private static final Logger log = Logger.getLogger(AbstractFormatterRunnable.class.getName());

	/** {@link SortedSet} containing document offset ranges which should be formatted. */
	protected final SortedSet<Pair<Integer, Integer>> changedElements;

	/** The {@link StyledDocument} from which the content should be formatted. */
	protected final StyledDocument document;

	/** End offset to format. Only used for selections. */
	protected final int endOffset;

	/** Start offset to format. Only used for selections. */
	protected final int startOffset;

	/**
	 * Constructor which has to be used by subclasses.
	 *
	 * @param document        the {@link StyledDocument} from which the content should be formatted
	 * @param startOffset     the start offset, will be set to zero if equal to {@code endOffset}
	 * @param endOffset       the end offset, will be set to the documents length minus one if equal to {@code startOffset}
	 * @param changedElements {@link SortedSet} containing document offset ranges which should be formatted or {@code null} to format the whole document
	 */
	protected AbstractFormatterRunnable(StyledDocument document, int startOffset, int endOffset, SortedSet<Pair<Integer, Integer>> changedElements) {
		this.document = document;
		this.changedElements = changedElements;

		if (startOffset != endOffset) {
			this.startOffset = Math.min(endOffset, startOffset);
			this.endOffset = Math.max(endOffset, startOffset);
		} else {
			this.startOffset = 0;
			this.endOffset = document.getLength() - 1;
		}
	}

	/**
	 * Applies the given {@code formattedContent} to the {@code document}. If
	 * {@code preserveBreakpoints} is {@code true} the {@link ClassLoadUnloadBreakpoint},
	 * {@link FieldBreakpoint} and {@link MethodBreakpoint} will be preserved, but
	 * {@link LineBreakpoint}s will get removed anyway.
	 *
	 * @param code                the previous (unformatted) content
	 * @param formattedContent    the formatted code
	 * @param preserveBreakpoints {@code true} to preserve all {@link ClassLoadUnloadBreakpoint}s,
	 *                            {@link FieldBreakpoint}s and {@link MethodBreakpoint}s
	 *                            (but not {@link LineBreakpoint}s)
	 *
	 * @return {@code true} if and only if the given {@code formattedContent} was set to
	 *         the {@code document}, if due to any circumstances (old code equals formatted code,
	 *         thrown exceptions, ...) the {@code formattedContent} wasn't applied {@code false}
	 *         is returned
	 */
	protected boolean setFormattedCode(String code, String formattedContent, boolean preserveBreakpoints) {
		// quick check for changed
		if (formattedContent != null && /* does not support changes of EOL */ !formattedContent.equals(code)) {
			DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();
			List<Breakpoint> breakpoint2Keep = Collections.emptyList();
			if (preserveBreakpoints) {
				Breakpoint[] breakpoints = debuggerManager.getBreakpoints();
				//a) remove all line breakpoints before replacing the text in the editor
				//b) hold all other breakpoints from the current file, so that they can be reattached
				//FIXME guess the main class by its filepath relative to src/com/foo/Bar.java -> com.foo.Bar
				FileObject fileObject = NbEditorUtilities.getFileObject(document);
				String classNameOfTopMostTypeInFile = getFQNOfTopMostType(fileObject);
				int lineStart = NbDocument.findLineNumber(document, startOffset);
				int lineEnd = NbDocument.findLineNumber(document, endOffset);
				List<LineBreakpoint> lineBreakPoints = getLineBreakpoints(breakpoints, fileObject, lineStart, lineEnd);
				for (LineBreakpoint breakpoint : lineBreakPoints) {
					debuggerManager.removeBreakpoint(breakpoint);
				}

				breakpoint2Keep = getPreserveableBreakpoints(breakpoints, classNameOfTopMostTypeInFile);

				//Remove all breakpoints from the current file (else they would be invalided)
				for (Breakpoint breakpoint : breakpoint2Keep) {
					debuggerManager.removeBreakpoint(breakpoint);
				}
			}

			try {
				GuardedSectionManager guards = GuardedSectionManager.getInstance(document);

				//runAtomicAsUser, so that removal and insert is only one undo step
				NbDocument.runAtomicAsUser(document, () -> {
					if (guards == null) {
						try {
							document.remove(0, code.length());
							document.insertString(0, formattedContent, null);
						} catch (BadLocationException ex) {
							Exceptions.printStackTrace(ex);
						}
					} else {
						log.log(Level.FINEST, "Formatted code: ''{0}''", formattedContent);

						final MutableInt endFormattedCode = new MutableInt(formattedContent.length() - 1);

						List<GuardedSection> guardsList = new ArrayList<>();
						Iterable<GuardedSection> guardedSections = guards.getGuardedSections();
						guardedSections.forEach(guard -> guardsList.add(guard));

						for (int i = guardsList.size() - 1; i >= -1; i--) {
							// find code between guards an replace it ...
							GuardedSection guard = null;
							if (i >= 0) {
								guard = guardsList.get(i);
							}

							int startNextGuard = code.length();
							if (i + 1 < guardsList.size()) {
								startNextGuard = guardsList.get(i + 1).getStartPosition().getOffset();
							}

							int endOldCode = startNextGuard - 1;

							int guardStart = -1;
							int startFormattedCode = 0;
							int startOldCode = 0;

							if (guard != null) {
								// do not use guard.getText() because it sometimes returns code without new line characters, e.g. for generated action methods
								String guardedCode = code.substring(guard.getStartPosition().getOffset(), guard.getEndPosition().getOffset() + 1);

								log.log(Level.FINEST, "Guard {0}: ''{1}''", new Object[] { guard.getName(), guardedCode });

								// guarded code is not formatted, so it can be found
								// by it's former formatting in the formatted code
								guardStart = formattedContent.indexOf(guardedCode);
								if (guardStart == -1) {
									log.log(Level.FINEST, "Could not find guarded code ''{0}'' after reformat into ''{1}''", new Object[] { guardedCode, formattedContent });

									continue;
								}

								startFormattedCode = guardStart + guardedCode.length();
								startOldCode = guard.getEndPosition().getOffset() + 1;
							}

							String formattedCodePart = formattedContent.substring(startFormattedCode, endFormattedCode.getValue() + 1);

							log.log(Level.FINEST, "Formatted code part ({0}-{1}/{2}): ''{3}''", new Object[] { startFormattedCode, endFormattedCode.getValue(), formattedCodePart.length(), formattedCodePart });

							try {
								String unformattedCodePart = document.getText(startOldCode, (endOldCode - startOldCode) + 1);

								log.log(Level.FINEST, "Previous code part ({0}-{1}/{2}): ''{3}''", new Object[] { startOldCode, endOldCode, (endOldCode - startOldCode) + 1, unformattedCodePart });

								if (!Objects.equals(unformattedCodePart, formattedCodePart)) {
									// to avoid loosing unguarded sections before or after a guarded section,
									// we insert the formatted code first and then remove the old one
									document.insertString(startOldCode, formattedCodePart, null);
									document.remove(startOldCode + formattedCodePart.length(), (endOldCode - startOldCode) + 1);
								}
							} catch (BadLocationException ex) {
								Exceptions.printStackTrace(ex);
							}

							endFormattedCode.setValue(guardStart - 1);
						}
					}
				});
			} catch (BadLocationException ex) {
				Exceptions.printStackTrace(ex);
				return false;
			}

			if (preserveBreakpoints) {
				//Reattach breakpoints where possible
				for (Breakpoint breakpoint : breakpoint2Keep) {
					debuggerManager.addBreakpoint(breakpoint);
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns a {@link List} within all {@link SourceGroup} objects for the given
	 * {@link Project}.
	 *
	 * @param p the {@link Project}
	 *
	 * @return A {@link List} within all {@link SourceGroup} objects for the given
	 *         {@link Project}
	 */
	@NotNull
	protected List<SourceGroup> getAllSourceGroups(Project p) {
		Sources sources = ProjectUtils.getSources(p);

		List<SourceGroup> list = new ArrayList<>();
		list.addAll(Arrays.asList(sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)));
		list.addAll(Arrays.asList(sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES)));
		list.addAll(Arrays.asList(sources.getSourceGroups(JavaProjectConstants.SOURCES_HINT_TEST)));
		list.addAll(Arrays.asList(sources.getSourceGroups(JavaProjectConstants.SOURCES_HINT_MAIN)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MavenSourcesImpl.TYPE_GEN_SOURCES)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MavenSourcesImpl.TYPE_OTHER)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MavenSourcesImpl.TYPE_TEST_OTHER)));

		return list;
	}

	/**
	 * Returns the fully qualified name of the top most type of the given {@link FileObject}.
	 *
	 * @param fo the {@link FileObject}
	 *
	 * @return The fully qualified name of the top most type of the given {@link FileObject}
	 */
	protected String getFQNOfTopMostType(FileObject fo) {
		if (null == fo) {
			return "";
		}

		Project p = FileOwnerQuery.getOwner(fo);
		if (null == p) {
			return "";
		}

		for (SourceGroup sourceGroup : getAllSourceGroups(p)) {
			//SourceGroup: c:/myprojects/project/src/main/java/
			//OriginFolder: c:/myprojects/project/src/main/java/com/foo/impl
			//Result: com/foo/impl (!=null so we found the source root)
			final FileObject rootFolder = sourceGroup.getRootFolder();
			if (null == rootFolder) {
				continue;
			}

			String relative = FileUtil.getRelativePath(rootFolder, fo);
			if (null != relative) {
				String result = relative.replaceAll("/", ".");
				if (result.toLowerCase().endsWith(".java")) {
					result = result.substring(0, result.length() - ".java".length());
				}

				return result;
			}
		}

		return "";
	}

	/**
	 * Returns a {@link List} within {@link LineBreakpoint}s from the given {@code breakpints}
	 * array which belong to the given {@code fileOfCurrentClass} that are between the given
	 * {@code lineStart} and {@code lineEnd}.
	 * 
	 * @param breakpoints        mixed array of {@link Breakpoint}s
	 * @param fileOfCurrentClass the {@link FileObject} of the currently formatted Java class
	 * @param lineStart          the line number where the formatting starts
	 * @param lineEnd            the line number where the formatting ends
	 *
	 * @return A {@link List} within {@link LineBreakpoint}s from the given {@code breakpints}
	 *         array which belong to the given {@code fileOfCurrentClass} that are between the given
	 *         {@code lineStart} and {@code lineEnd}
	 */
	protected List<LineBreakpoint> getLineBreakpoints(Breakpoint[] breakpoints, FileObject fileOfCurrentClass, int lineStart, int lineEnd) {
		List<LineBreakpoint> result = new ArrayList<>();
		for (Breakpoint breakpoint : breakpoints) {
			/**
			 * NOTE: ExceptionBreakpoint/ThreadBreakpoint have no annotation in
			 * file, so they cannot be removed by the formatter
			 */
			/**
			 * Remove LineBreakpoints, because setting the new text for the
			 * document invalidates the breakpoints
			 */
			if (breakpoint instanceof LineBreakpoint) {
				LineBreakpoint lineBreakpoint = (LineBreakpoint) breakpoint;
				String url = lineBreakpoint.getURL();
				if (null == url) {
					continue;
				}

				int current = lineBreakpoint.getLineNumber();
				final boolean isBreakpointInSelection = lineStart <= current && current <= lineEnd;
				if (!isBreakpointInSelection) {
					continue;
				}

				if (url.startsWith("jar:file:")) {
					//https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/80 
					//prevent URI is not hierarchical.
					continue;
				}

				FileObject toFileObject;
				try {
					toFileObject = FileUtil.toFileObject(FileUtil.normalizeFile(Utilities.toFile(new URI(url))));
				} catch (Exception ex) {
					log.log(Level.WARNING,
							"{0} cannot be converted to URI/File: {1}. Please see https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/55 and report to https://github.com/funfried/externalcodeformatter_for_netbeans/issues/",
							new Object[] { url, ex.getMessage() });
					continue;
				}

				if (null == toFileObject) {
					continue;
				}

				if (toFileObject.equals(fileOfCurrentClass)) {
					result.add(lineBreakpoint);
				}
			}
		}

		return result;
	}

	/**
	 * Returns a {@link List} within all preservable {@link Breakpoint}s which belong
	 * to the given {@code currentClassName} from a given mixed {@link Breakpoint}s
	 * array.
	 *
	 * @param breakpoints      the mixed {@link Breakpoint}s array
	 * @param currentClassName the fully qualified class name of the Java class that
	 *                         is currently formatted
	 *
	 * @return A {@link List} within all preservable {@link Breakpoint}s which belong
	 *         to the given {@code currentClassName} from a given mixed {@link Breakpoint}s
	 *         array
	 */
	protected List<Breakpoint> getPreserveableBreakpoints(Breakpoint[] breakpoints, String currentClassName) {
		List<Breakpoint> result = new ArrayList<>();
		for (Breakpoint breakpoint : breakpoints) {
			if (breakpoint instanceof ClassLoadUnloadBreakpoint) {
				for (String classname : ((ClassLoadUnloadBreakpoint) breakpoint).getClassFilters()) {
					if (isSameTypeOrInnerType(classname, currentClassName)) {
						result.add(breakpoint);
					}
				}
			}

			if (breakpoint instanceof FieldBreakpoint) {
				if (isSameTypeOrInnerType(((FieldBreakpoint) breakpoint).getClassName(), currentClassName)) {
					result.add(breakpoint);
				}
			}

			if (breakpoint instanceof MethodBreakpoint) {
				for (String className : ((MethodBreakpoint) breakpoint).getClassFilters()) {
					if (isSameTypeOrInnerType(className, currentClassName)) {
						result.add(breakpoint);
					}
				}
			}

			/**
			 * NOTE: ExceptionBreakpoint/ThreadBreakpoint have no annotation in
			 * file, so they cannot be removed by the formatter
			 */
			/**
			 * NOTE: LineBreakpoint is not supported
			 */
		}

		return result;
	}

	/**
	 * Checks if the given {@code className} is the same or inner type of the given
	 * {@code fqnOfTopMostType}. Returns {@code true} if the given {@code className}
	 * is the same or an inner type of the given {@code fqnOfTopMostType}.
	 * 
	 * @param className        the class name to check
	 * @param fqnOfTopMostType the top most type to check against
	 *
	 * @return {@code true} if the given {@code className} is the same or an inner
	 *         type of the given {@code fqnOfTopMostType}
	 */
	protected static boolean isSameTypeOrInnerType(String className, String fqnOfTopMostType) {
		if (null == className) {
			return false;
		}

		if (null == fqnOfTopMostType) {
			return false;
		}

		if (className.equals(fqnOfTopMostType)) {
			return true;
		}

		//Support innerTypes like com.company.Foo$InnerClass
		return className.startsWith(fqnOfTopMostType + "$");
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
	 * which describe the start and end offsets which can be formatted in respect to
	 * the given {@link GuardedSectionManager}.
	 *
	 * @param code   the current unformatted content of the {@link document}
	 * @param guards the {@link GuardedSectionManager} of the {@link document}
	 *
	 * @return A {@link SortedSet} within ranges as {@link Pair}s of {@link Integer}s
	 *         which describe the start and end offsets which can be formatted in respect to
	 *         the given {@link GuardedSectionManager}
	 */
	protected SortedSet<Pair<Integer, Integer>> getFormattableSections(String code, GuardedSectionManager guards) {
		SortedSet<Pair<Integer, Integer>> regions = changedElements;
		if (CollectionUtils.isEmpty(changedElements)) {
			regions = new TreeSet<>();

			if (this.startOffset > -1 && this.endOffset > -1) {
				regions.add(Pair.of(this.startOffset, this.endOffset));
			} else {
				regions.add(Pair.of(0, code.length() - 1));
			}
		}

		if (guards != null) {
			SortedSet<Pair<Integer, Integer>> nonGuardedSections = new TreeSet<>();
			Iterable<GuardedSection> guardedSections = guards.getGuardedSections();

			StringBuilder sb = new StringBuilder();
			guardedSections.forEach(guard -> sb.append(guard.getStartPosition().getOffset()).append("/").append(guard.getEndPosition().getOffset()).append(" "));
			log.log(Level.FINEST, "Guarded sections: {0}", sb.toString().trim());

			for (Pair<Integer, Integer> changedElement : regions) {
				nonGuardedSections.addAll(avoidGuardedSection(changedElement, guardedSections));
			}

			regions = nonGuardedSections;
		}

		StringBuilder sb = new StringBuilder();
		regions.stream().forEach(section -> sb.append(section.getLeft()).append("/").append(section.getRight()).append(" "));
		log.log(Level.FINEST, "Formating sections: {0}", sb.toString().trim());

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
							start.setValue(guardedSection.getEndPosition().getOffset() + 1);
						} else {
							start.setValue(null);
							end.setValue(null);

							throw new BreakException();
						}
					} else if (end.getValue() > guardedSection.getStartPosition().getOffset() && end.getValue() <= guardedSection.getEndPosition().getOffset()) {
						if (start.getValue() <= guardedSection.getStartPosition().getOffset()) {
							end.setValue(guardedSection.getStartPosition().getOffset() - 1);
						} else {
							start.setValue(null);
							end.setValue(null);

							throw new BreakException();
						}
					} else if (start.getValue() < guardedSection.getStartPosition().getOffset() && end.getValue() > guardedSection.getEndPosition().getOffset()) {
						ret.add(Pair.of(start.getValue(), guardedSection.getStartPosition().getOffset() - 1));

						start.setValue(guardedSection.getEndPosition().getOffset() + 1);
					}
				});
			} catch (BreakException ex) {
				// found no better solution to break a forEach
			}
		}

		if (start.getValue() != null && end.getValue() != null) {
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
