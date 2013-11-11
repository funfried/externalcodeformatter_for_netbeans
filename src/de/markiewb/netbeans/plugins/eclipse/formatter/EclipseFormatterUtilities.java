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
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor != null) {
            caret = editor.getCaretPosition();
        }
        //run atomic to prevent empty entries in undo buffer
        NbDocument.runAtomic(document, new Runnable() {

            @Override
            public void run() {
                try {
                    final int length = document.getLength();

                    String docText = null;
                    try {
                        docText = document.getText(0, length);
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    final String formattedContent = formatter.forCode(docText);

                    document.remove(0, length);
                    document.insertString(0, formattedContent, null);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        if (editor != null) {
            editor.setCaretPosition(Math.max(0, Math.min(caret, document.getLength())));
        }
    }

    public void reformatWithNetBeans(final StyledDocument styledDoc) {
        final Reformat rf = Reformat.get(styledDoc);
        rf.lock();
        try {
            NbDocument.runAtomicAsUser(styledDoc, new Runnable() {
                @Override
                public void run() {
                    try {
                        rf.reformat(0, styledDoc.getLength());
                    } catch (BadLocationException ex) {
                    }
                }
            });
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            rf.unlock();
        }
    }

    public static boolean isJava(Document document) {
        return "text/x-java".equals(NbEditorUtilities.getMimeType(document));
    }
 
}
