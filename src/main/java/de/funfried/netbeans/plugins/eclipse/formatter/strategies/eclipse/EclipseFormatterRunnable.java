/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.eclipse.formatter.strategies.eclipse;

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
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

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
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

import de.funfried.netbeans.plugins.eclipse.formatter.Utils;
import de.funfried.netbeans.plugins.eclipse.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.eclipse.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.eclipse.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.eclipse.formatter.options.Settings;

/**
 * Formats the given document using the eclipse formatter. LineBreakpoints get
 * removed and the following breakpoints are getting reattached:
 * <ul>
 * <li>ClassLoadUnloadBreakpoint</li>
 * <li>FieldBreakpoint</li>
 * <li>MethodBreakpoint</li>
 * </ul>
 */
class EclipseFormatterRunnable implements Runnable {
	private static final Logger log = Logger.getLogger(EclipseFormatterRunnable.class.getName());

	/**
	 * Copied from org.netbeans.modules.maven.classpath.MavenSourcesImpl. These
	 * constants where not public API, so they are duplicated in here.
	 * https://github.com/markiewb/nb-resource-hyperlink-at-cursor/issues/9
	 */
	public static final String MAVEN_TYPE_OTHER = "Resources"; //NOI18N

	public static final String MAVEN_TYPE_TEST_OTHER = "TestResources"; //NOI18N

	public static final String MAVEN_TYPE_GEN_SOURCES = "GeneratedSources"; //NOI18N

	/**
	 * http://bits.netbeans.org/dev/javadoc/org-netbeans-modules-java-project/constant-values.html#org.netbeans.api.java.project.SOURCES_HINT_TEST
	 *
	 */
	public static final String SOURCES_HINT_MAIN = "main";

	public static final String SOURCES_HINT_TEST = "test";

	public static final String SOURCES_TYPE_JAVA = "java";

	public static final String SOURCES_TYPE_RESOURCES = "resources";

	private final SortedSet<Pair<Integer, Integer>> changedElements;

	private final StyledDocument document;

	private final int endOffset;

	private final FileObject fileObject;

	private final EclipseFormatter formatter;

	private final int startOffset;

	EclipseFormatterRunnable(StyledDocument document, EclipseFormatter formatter, int dot, int mark, SortedSet<Pair<Integer, Integer>> changedElements) {
		this.document = document;
		this.fileObject = NbEditorUtilities.getFileObject(document);
		this.formatter = formatter;
		this.changedElements = changedElements;

		if (dot != mark) {
			this.startOffset = Math.min(mark, dot);
			this.endOffset = Math.max(mark, dot);
		} else {
			this.startOffset = 0;
			this.endOffset = document.getLength() - 1;
		}
	}

	@Override
	public void run() {
		boolean isJava = Utils.isJava(document);
		if (!isJava) {
			throw new FileTypeNotSupportedException("The file type '" + NbEditorUtilities.getMimeType(document) + "' is not supported by the Eclipse Java Code Formatter");
		}

		Preferences pref = Settings.getActivePreferences(document);

		String formatterFilePref = getFormatterFileFromProjectConfiguration(pref.getBoolean(Settings.USE_PROJECT_PREFS, true), document);
		if (null == formatterFilePref) {
			formatterFilePref = pref.get(Settings.ECLIPSE_FORMATTER_LOCATION, null);
		}

		final String formatterFile = formatterFilePref;
		final String formatterProfile = pref.get(Settings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
		String lineFeedSetting = pref.get(Settings.LINEFEED, "");
		String sourceLevel = pref.get(Settings.SOURCELEVEL, "");
		boolean preserveBreakpoints = pref.getBoolean(Settings.PRESERVE_BREAKPOINTS, true);

		//save with configured linefeed
		String lineFeed = Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
		if (null != lineFeedSetting) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		final String code;

		try {
			code = document.getText(0, document.getLength());
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
			return;
		}

		try {
			SortedSet<Pair<Integer, Integer>> regions = changedElements;
			if (CollectionUtils.isEmpty(changedElements)) {
				regions = new TreeSet<>();

				if (this.startOffset > -1 && this.endOffset > -1) {
					regions.add(Pair.of(this.startOffset, this.endOffset));
				} else {
					regions.add(Pair.of(0, code.length() - 1));
				}
			}

			GuardedSectionManager guards = GuardedSectionManager.getInstance(document);
			if (guards != null) {
				SortedSet<Pair<Integer, Integer>> nonGuardedSections = new TreeSet<>();
				Iterable<GuardedSection> guardedSections = guards.getGuardedSections();

				for (Pair<Integer, Integer> changedElement : regions) {
					nonGuardedSections.addAll(avoidGuardedSection(changedElement, guardedSections));
				}

				regions = nonGuardedSections;
			}

			String formattedContent = formatter.format(formatterFile, formatterProfile, code, lineFeedSetting, sourceLevel, regions);
			// quick check for changed
			if (formattedContent != null && /* does not support changes of EOL */ !formattedContent.equals(code)) {
				DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();
				List<Breakpoint> breakpoint2Keep = Collections.emptyList();
				if (preserveBreakpoints) {
					final Breakpoint[] breakpoints = debuggerManager.getBreakpoints();
					//a) remove all line breakpoints before replacing the text in the editor
					//b) hold all other breakpoints from the current file, so that they can be reattached
					//FIXME guess the main class by its filepath relative to src/com/foo/Bar.java -> com.foo.Bar
					final String classNameOfTopMostTypeInFile = getFQNOfTopMostType(fileObject);
					int lineStart = NbDocument.findLineNumber(document, startOffset);
					int lineEnd = NbDocument.findLineNumber(document, endOffset);
					List<Breakpoint> lineBreakPoints = getLineBreakpoints(breakpoints, fileObject, lineStart, lineEnd);
					for (Breakpoint breakpoint : lineBreakPoints) {
						debuggerManager.removeBreakpoint(breakpoint);
					}
					breakpoint2Keep = getPreserveableBreakpoints(breakpoints, classNameOfTopMostTypeInFile);
					//Remove all breakpoints from the current file (else they would be invalided)
					for (Breakpoint breakpoint : breakpoint2Keep) {
						debuggerManager.removeBreakpoint(breakpoint);
					}
				}

				try {
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
									String guardedCode = guard.getText();

									// guarded code is not formatted, so it can be found
									// by it's former formatting in the formatted code
									guardStart = formattedContent.indexOf(guardedCode);
									if (guardStart == -1) {
										continue;
									}

									// add an extra linefeed character to the guard, because it's not taken into account
									// in the guarded section, but throws an exception if it is removed from the document
									startFormattedCode = guardStart + guardedCode.length() + lineFeed.length();
									startOldCode = guard.getEndPosition().getOffset() + lineFeed.length();
								}

								String formattedCodePart = formattedContent.substring(startFormattedCode, endFormattedCode.getValue() + 1);

								try {
									String unformattedCodePart = document.getText(startOldCode, (endOldCode - startOldCode) + 1);
									if (!Objects.equals(unformattedCodePart, formattedCodePart)) {
										// to avoid loosing unguarded sections before or after a guarded section,
										// we insert the formatted code first and then remove the old one
										document.insertString(startOldCode, formattedCodePart, null);
										document.remove(startOldCode + (formattedCodePart.length() - 1), (endOldCode - startOldCode) + 1);
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
					return;
				}

				if (preserveBreakpoints) {
					//Reattach breakpoints where possible
					for (Breakpoint breakpoint : breakpoint2Keep) {
						debuggerManager.addBreakpoint(breakpoint);
					}
				}

				String msg = getNotificationMessageForEclipseFormatterConfigurationFileType(formatterFile, formatterProfile);

				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Eclipse formatter", Utils.iconEclipse, msg, null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using Eclipse formatter: " + msg);
				});
			}
		} catch (ProfileNotFoundException ex) {
			SwingUtilities.invokeLater(() -> {
				NotifyDescriptor notify = new NotifyDescriptor.Message(
						String.format("<html>Profile '%s' not found in <tt>%s</tt><br><br>Please configure a valid one in the project properties OR at Tools|Options|Java|Eclipse Formatter!", formatterProfile,
								formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);
			});

			throw ex;
		} catch (CannotLoadConfigurationException ex) {
			SwingUtilities.invokeLater(() -> {
				NotifyDescriptor notify = new NotifyDescriptor.Message(String.format("<html>Could not find configuration file %s.<br>Make sure the file exists and it can be read.", formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);
			});

			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		}
	}

	private List<SourceGroup> getAllSourceGroups(Project p) {
		Sources sources = ProjectUtils.getSources(p);

		List<SourceGroup> list = new ArrayList<>();
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_TYPE_JAVA)));
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_TYPE_RESOURCES)));
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_HINT_TEST)));
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_HINT_MAIN)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MAVEN_TYPE_GEN_SOURCES)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MAVEN_TYPE_OTHER)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MAVEN_TYPE_TEST_OTHER)));

		return list;
	}

	private String getFQNOfTopMostType(FileObject fo) {
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

	private List<Breakpoint> getLineBreakpoints(Breakpoint[] breakpoints, FileObject fileOfCurrentClass, int lineStart, int lineEnd) {
		List<Breakpoint> result = new ArrayList<>();
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
					log.log(Level.WARNING, "{0} cannot be converted to URI/File: {1}. Please report to https://github.com/funfried/eclipsecodeformatter_for_netbeans/issues/55",
							new Object[] { url, ex.getMessage() });
					continue;
				}

				if (null == toFileObject) {
					continue;
				}

				if (fileOfCurrentClass.equals(toFileObject)) {
					result.add(breakpoint);
				}
			}
		}

		return result;
	}

	private List<Breakpoint> getPreserveableBreakpoints(Breakpoint[] breakpoints, String currentClassName) {
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

	private static boolean isSameTypeOrInnerType(String className, String fqnOfTopMostType) {
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

	private String getFormatterFileFromProjectConfiguration(final boolean useProjectPrefs, final StyledDocument styledDoc) {
		//use ${projectdir}/.settings/org.eclipse.jdt.core.prefs, if activated in options
		if (useProjectPrefs) {
			FileObject fileForDocument = NbEditorUtilities.getFileObject(styledDoc);
			if (null != fileForDocument) {

				Project project = FileOwnerQuery.getOwner(fileForDocument);
				if (null != project) {
					FileObject projectDirectory = project.getProjectDirectory();
					FileObject preferenceFile = projectDirectory.getFileObject(".settings/" + Settings.PROJECT_PREF_FILE);
					if (null != preferenceFile) {
						return preferenceFile.getPath();
					}
				}
			}
		}
		return null;
	}

	private String getNotificationMessageForEclipseFormatterConfigurationFileType(String formatterFile, String formatterProfile) {
		String msg = "";
		if (Settings.isWorkspaceMechanicFile(formatterFile)) {
			//Workspace mechanic file
			msg = String.format("Using %s", formatterFile);
		} else if (Settings.isXMLConfigurationFile(formatterFile)) {
			//XML file
			msg = String.format("Using profile '%s' from %s", formatterProfile, formatterFile);
		} else if (Settings.isProjectSetting(formatterFile)) {
			//org.eclipse.jdt.core.prefs
			msg = String.format("Using %s", formatterFile);
		}
		return msg;
	}

	private SortedSet<Pair<Integer, Integer>> avoidGuardedSection(Pair<Integer, Integer> section, Iterable<GuardedSection> guardedSections) {
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

	private static class BreakException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
