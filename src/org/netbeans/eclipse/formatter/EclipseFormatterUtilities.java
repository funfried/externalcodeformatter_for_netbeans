package org.netbeans.eclipse.formatter;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

public class EclipseFormatterUtilities {

    public static Icon icon = ImageUtilities.image2Icon(ImageUtilities.loadImage("org/netbeans/eclipse/formatter/icon.png"));

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
                    if (editor != null && caret > -1) {
//                        editor.setCaretPosition(caret);
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
