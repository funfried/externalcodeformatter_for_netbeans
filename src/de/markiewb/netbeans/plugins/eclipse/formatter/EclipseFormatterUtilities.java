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

import de.markiewb.netbeans.plugins.eclipse.formatter.customizer.ProjectSpecificSettingsPanel;
import de.markiewb.netbeans.plugins.eclipse.formatter.options.EclipseFormatterPanel;
import java.util.prefs.Preferences;
import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

public class EclipseFormatterUtilities {

    @StaticResource
    private static final String eclipse = "de/markiewb/netbeans/plugins/eclipse/formatter/eclipse.gif";
    @StaticResource
    private static final String netBeans = "de/markiewb/netbeans/plugins/eclipse/formatter/netbeans.gif";

    public static Icon iconEclipse = ImageUtilities.image2Icon(ImageUtilities.loadImage(eclipse));
    public static Icon iconNetBeans = ImageUtilities.image2Icon(ImageUtilities.loadImage(netBeans));

    public static EclipseFormatter getEclipseFormatter(String formatterFile) {
        return new EclipseFormatter(formatterFile);
    }

    public void reFormatWithEclipse(StyledDocument document, EclipseFormatter formatter) {
        int caret = -1;
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor != null) {
            caret = editor.getCaretPosition();
        }
        final int length = document.getLength();
        String result = null;
        try {
            String docText = null;
            try {
                docText = document.getText(0, length);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            result = formatter.forCode(docText);
        } finally {
            if (result != null) {
                try {
                    document.remove(0, length);
                    document.insertString(0, result, null);
                    if (editor != null) {
                        editor.setCaretPosition(Math.max(0, Math.min(caret, document.getLength())));
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
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
