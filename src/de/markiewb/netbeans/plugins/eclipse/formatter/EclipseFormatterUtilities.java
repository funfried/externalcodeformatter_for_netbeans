/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 */
package de.markiewb.netbeans.plugins.eclipse.formatter;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

public class EclipseFormatterUtilities {

    @StaticResource
    private static final String eclipse = "de/markiewb/netbeans/plugins/eclipse/formatter/eclipse.gif";
    @StaticResource
    private static final String netBeans = "de/markiewb/netbeans/plugins/eclipse/formatter/netbeans.gif";

    public static Icon iconEclipse = ImageUtilities.image2Icon(ImageUtilities.loadImage(eclipse));
    public static Icon iconNetBeans = ImageUtilities.image2Icon(ImageUtilities.loadImage(netBeans));

    public static EclipseFormatter getEclipseFormatter(String formatterFile, String formatterProfile) {
        return new EclipseFormatter(formatterFile, formatterProfile);
    }

    public void reFormatWithEclipse(final StyledDocument document, final EclipseFormatter formatter) {
        int caret = -1;
        int dot = -1;
        int mark = -1;
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor != null) {
            caret = editor.getCaretPosition();
            dot = editor.getCaret().getDot();
            mark = editor.getCaret().getMark();
        }
        //run atomic to prevent empty entries in undo buffer
        NbDocument.runAtomic(document, new EclipseFormatterTask(document, formatter, dot, mark));
        if (editor != null) {
            editor.setCaretPosition(Math.max(0, Math.min(caret, document.getLength())));
        }
    }

    public void reformatWithNetBeans(final StyledDocument styledDoc) {
        final Reformat rf = Reformat.get(styledDoc);
        int dot = -1;
        int mark = -1;
        rf.lock();
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor != null) {
            dot = editor.getCaret().getDot();
            mark = editor.getCaret().getMark();
        }

        try {
            NbDocument.runAtomicAsUser(styledDoc, new NetBeansFormatterTask(styledDoc, rf, dot, mark));
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            rf.unlock();
        }
    }

    public static boolean isJava(Document document) {
        return "text/x-java".equals(NbEditorUtilities.getMimeType(document));
    }

    private static class EclipseFormatterTask implements Runnable {

        private final StyledDocument document;
        private final EclipseFormatter formatter;
        private final int startOffset;
        private final int endOffset;
        private final boolean full;

        EclipseFormatterTask(StyledDocument document, EclipseFormatter formatter, int dot, int mark) {
            this.document = document;
            this.formatter = formatter;
            if (dot != mark) {
                full = false;
                if (dot > mark) {
                    startOffset = mark;
                    endOffset = dot;
                } else {
                    startOffset = dot;
                    endOffset = mark;
                }
            } else {
                full = true;
                startOffset = 0;
                endOffset = document.getLength();
            }
        }

        @Override
        public void run() {
            try {
                String docText = null;
                try {
                    docText = document.getText(0, document.getLength());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                    return;
                }
                
                final String formattedContent = formatter.forCode(docText, startOffset, endOffset);
                
                // quick check for changed
                if (formattedContent != null) {
                    document.remove(startOffset, endOffset - startOffset);
                    document.insertString(startOffset, 
                            formattedContent.substring(startOffset, 
                                    endOffset + formattedContent.length() - 
                                            docText.length()), null);
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private static class NetBeansFormatterTask implements Runnable {

        private final StyledDocument document;
        private final Reformat rf;
        private final int startOffset;
        private final int endOffset;

        NetBeansFormatterTask(StyledDocument document, Reformat rf, int dot, int mark) {
            this.document = document;
            this.rf = rf;
            if (dot != mark) {
                if (dot > mark) {
                    startOffset = mark;
                    endOffset = dot;
                } else {
                    startOffset = dot;
                    endOffset = mark;
                }
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
