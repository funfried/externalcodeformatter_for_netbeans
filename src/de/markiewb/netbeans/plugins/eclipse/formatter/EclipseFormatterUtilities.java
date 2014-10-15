/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 *    Saad Mufti <saad.mufti@teamaol.com> 
 */
package de.markiewb.netbeans.plugins.eclipse.formatter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.ClassLoadUnloadBreakpoint;
import org.netbeans.api.debugger.jpda.FieldBreakpoint;
import org.netbeans.api.debugger.jpda.LineBreakpoint;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

public class EclipseFormatterUtilities {

    private static final Logger LOG = Logger.getLogger(EclipseFormatterUtilities.class.getName());

    @StaticResource
    private static final String eclipse = "de/markiewb/netbeans/plugins/eclipse/formatter/eclipse.gif";
    @StaticResource
    private static final String netBeans = "de/markiewb/netbeans/plugins/eclipse/formatter/netbeans.gif";

    public static Icon iconEclipse = ImageUtilities.image2Icon(ImageUtilities.loadImage(eclipse));
    public static Icon iconNetBeans = ImageUtilities.image2Icon(ImageUtilities.loadImage(netBeans));

    private static final RequestProcessor RP = new RequestProcessor("Format with Eclipse formatter", 1, true, false); //NOI18N

    public static EclipseFormatter getEclipseFormatter(String formatterFile, String formatterProfile) {
        return new EclipseFormatter(formatterFile, formatterProfile);
    }

    /**
     *
     * @param document
     * @param formatter
     * @param forSave true, if invoked by save action
     */
    public void reFormatWithEclipse(final StyledDocument document, final EclipseFormatter formatter, boolean forSave, final boolean preserveBreakpoints) {
        int caret = -1;
        int dot = -1;
        int mark = -1;
        final JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor != null) {
            caret = editor.getCaretPosition();
            // only look for selection if reformatting due to menu action, if reformatting on save we always reformat the whole doc
            if (!forSave) {
                dot = editor.getCaret().getDot();
                mark = editor.getCaret().getMark();
            }
        }

        final int _caret = caret;
        final int _dot = dot;
        final int _mark = mark;
        RP.post(new EclipseFormatterTask(document, formatter, _dot, _mark, preserveBreakpoints, _caret, editor));
    }

    /**
     *
     * @param document
     * @param forSave true, if invoked by save action
     */
    public void reformatWithNetBeans(final StyledDocument document, boolean forSave) {
        final Reformat rf = Reformat.get(document);
        int dot = -1;
        int mark = -1;
        rf.lock();
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        // only care about selection if reformatting on menu action and not on file save
        if ((editor != null) && !forSave) {
            dot = editor.getCaret().getDot();
            mark = editor.getCaret().getMark();
        }

        try {
            NbDocument.runAtomicAsUser(document, new NetBeansFormatterTask(document, rf, dot, mark));
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            rf.unlock();
        }
    }

    public static boolean isJava(Document document) {
        return "text/x-java".equals(NbEditorUtilities.getMimeType(document));
    }

    /**
     * Formats the given document using the eclipse formatter. LineBreakpoints
     * get removed and the following breakpoints are getting reattached:
     * <ul>
     * <li>ClassLoadUnloadBreakpoint</li>
     * <li>FieldBreakpoint</li>
     * <li>MethodBreakpoint</li>
     * </ul>
     */
    private static class EclipseFormatterTask implements Runnable {

        private static boolean isSameTypeOrInnerType(String className, String fqnOfTopMostType) {
            if (className.equals(fqnOfTopMostType)) {
                return true;
            }
            //Support innerTypes like com.company.Foo$InnerClass
            return className.startsWith(fqnOfTopMostType + "$");
        }

        private final int caret;

        private final StyledDocument document;
        private final JTextComponent editor;
        private final int endOffset;
        private final FileObject fileObject;
        private final EclipseFormatter formatter;
        private final boolean preserveBreakpoints;
        private final int startOffset;

        EclipseFormatterTask(StyledDocument document, EclipseFormatter formatter, int dot, int mark, boolean preserveBreakpoints, int caret, JTextComponent editor) {
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
            this.caret = caret;
            this.editor = editor;
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
                final String formattedContent = formatter.forCode(docText, startOffset, endOffset);

                // quick check for changed
                if (formattedContent != null && /*does not support changes of EOL*/ !formattedContent.equals(docText)) {
                    DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();
                    List<Breakpoint> breakpoint2Keep = Collections.emptyList();
                    if (preserveBreakpoints) {
                        final Breakpoint[] breakpoints = debuggerManager.getBreakpoints();
                        //a) remove all line breakpoints before replacing the text in the editor
                        //b) hold all other breakpoints from the current file, so that they can be reattached
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
                    NbDocument.runAtomicAsUser(document, new Runnable() {

                        @Override
                        public void run() {
                            try {
                                document.remove(startOffset, endOffset - startOffset);
                                document.insertString(startOffset,
                                        formattedContent.substring(startOffset,
                                                endOffset + formattedContent.length()
                                                - docText.length()), null);
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
                    //Set caret if possible
                    if (editor != null) {
                        editor.setCaretPosition(Math.max(0, Math.min(caret, document.getLength())));
                    }
                }

            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private String getFQNOfTopMostType(FileObject fo) throws IllegalArgumentException {
            final JavaSource javaSource = JavaSource.forFileObject(fo);

            final List<String> collector = new ArrayList<>();
            final org.netbeans.api.java.source.Task<CompilationController> task = new org.netbeans.api.java.source.Task<CompilationController>() {
                @Override
                public void run(CompilationController cc) throws IOException {
                    cc.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);

                    //get FQN of outermost type
                    List<? extends TypeElement> topLevelElements = cc.getTopLevelElements();
                    if (null != topLevelElements && !topLevelElements.isEmpty()) {
                        TypeElement outermostTypeElement = cc.getElementUtilities().outermostTypeElement(topLevelElements.get(0));
                        collector.add(outermostTypeElement.getQualifiedName().toString());
                    }
                }
            };
            try {
                javaSource.runUserActionTask(task, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            if (collector.isEmpty()) {
                return null;
            }
            return collector.get(0);
        }

        private List<Breakpoint> getLineBreakpoints(Breakpoint[] breakpoints, FileObject fileOfCurrentClass, int lineStart, int lineEnd) throws IllegalArgumentException {
            List<Breakpoint> result = new ArrayList<>();
            for (Breakpoint breakpoint : breakpoints) {
                /**
                 * NOTE: ExceptionBreakpoint/ThreadBreakpoint have no annotation
                 * in file, so they cannot be removed by the formatter
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
                    FileObject toFileObject;
                    try {
                        toFileObject = FileUtil.toFileObject(FileUtil.normalizeFile(Utilities.toFile(new URI(url))));
                    } catch (Exception ex) {
                        LOG.log(Level.WARNING, "{0} cannot be converted to URI/File: {1}. Please report to https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/55", new Object[]{url, ex.getMessage()});
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
                 * NOTE: ExceptionBreakpoint/ThreadBreakpoint have no annotation
                 * in file, so they cannot be removed by the formatter
                 */
                /**
                 * NOTE: LineBreakpoint is not supported
                 */
            }

            return result;
        }

    }

    private static class NetBeansFormatterTask implements Runnable {

        private final Reformat rf;
        private final int startOffset;
        private final int endOffset;

        NetBeansFormatterTask(StyledDocument document, Reformat rf, int dot, int mark) {
            this.rf = rf;
            if (dot != mark) {
                startOffset = Math.min(mark, dot);
                endOffset = Math.max(mark, dot);
            } else {
                startOffset = 0;
                endOffset = document.getLength();
            }
        }

        @Override
        public void run() {
            try {
                rf.reformat(startOffset, endOffset);
            } catch (BadLocationException ex) {
            }
        }
    }

}
