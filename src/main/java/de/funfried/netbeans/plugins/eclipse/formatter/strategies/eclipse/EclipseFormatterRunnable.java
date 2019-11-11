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
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.ClassLoadUnloadBreakpoint;
import org.netbeans.api.debugger.jpda.FieldBreakpoint;
import org.netbeans.api.debugger.jpda.LineBreakpoint;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

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

	private static final Logger LOG = Logger.getLogger(EclipseFormatterRunnable.class.getName());

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

	private final SortedSet<Pair<Integer, Integer>> changedElements;

	private final StyledDocument document;

	private final int endOffset;

	private final FileObject fileObject;

	private final EclipseFormatter formatter;

	private final boolean preserveBreakpoints;

	private final int startOffset;

	EclipseFormatterRunnable(StyledDocument document, EclipseFormatter formatter, int dot, int mark, boolean preserveBreakpoints, SortedSet<Pair<Integer, Integer>> changedElements) {
		this.document = document;
		this.fileObject = NbEditorUtilities.getFileObject(document);
		this.formatter = formatter;
		if (dot != mark) {
			startOffset = Math.min(mark, dot);
			endOffset = Math.max(mark, dot);
		} else {
			startOffset = 0;
			endOffset = document.getLength();
		}
		this.preserveBreakpoints = preserveBreakpoints;
		this.changedElements = changedElements;
	}

	@Override
	public void run() {
		try {
			final String docText;
			try {
				docText = document.getText(0, document.getLength());
			} catch (BadLocationException ex) {
				Exceptions.printStackTrace(ex);
				return;
			}
			final String formattedContent = formatter.forCode(docText, startOffset, endOffset, changedElements);
			// quick check for changed
			if (formattedContent != null && /* does not support changes of EOL */ !formattedContent.equals(docText)) {
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
				//runAtomicAsUser, so that removal and insert is only one undo step
				NbDocument.runAtomicAsUser(document, new Runnable() {
					@Override
					public void run() {
						try {
							document.remove(startOffset, endOffset - startOffset);
							document.insertString(startOffset, formattedContent.substring(startOffset, endOffset + formattedContent.length() - docText.length()), null);
						} catch (BadLocationException ex) {
							Exceptions.printStackTrace(ex);
						}
					}
				});
				if (preserveBreakpoints) {
					//Reattach breakpoints where possible
					for (Breakpoint breakpoint : breakpoint2Keep) {
						debuggerManager.addBreakpoint(breakpoint);
					}
				}
			}
		} catch (Exception ex) {
			Exceptions.printStackTrace(ex);
		}
	}

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

	private List<SourceGroup> getAllSourceGroups(Project p) {
		final Sources sources = ProjectUtils.getSources(p);
		List<SourceGroup> list = new ArrayList<SourceGroup>();
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_TYPE_JAVA)));
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_TYPE_RESOURCES)));
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_HINT_TEST)));
		list.addAll(Arrays.asList(sources.getSourceGroups(SOURCES_HINT_MAIN)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MAVEN_TYPE_GEN_SOURCES)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MAVEN_TYPE_OTHER)));
		list.addAll(Arrays.asList(sources.getSourceGroups(MAVEN_TYPE_TEST_OTHER)));
		return list;
	}

	private String getFQNOfTopMostType(FileObject fo) throws IllegalArgumentException {
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

	private List<Breakpoint> getLineBreakpoints(Breakpoint[] breakpoints, FileObject fileOfCurrentClass, int lineStart, int lineEnd) throws IllegalArgumentException {
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
					LOG.log(Level.WARNING, "{0} cannot be converted to URI/File: {1}. Please report to https://github.com/funfried/eclipsecodeformatter_for_netbeans/issues/55",
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

	private List<Breakpoint> getPreserveableBreakpoints(Breakpoint[] breakpoints, String currentClassName) throws IllegalArgumentException {
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

}
