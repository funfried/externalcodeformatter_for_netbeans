package org.netbeans.eclipse.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.Icon;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.eclipse.formatter.options.EclipseFormatterPanel;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

public class EclipseFormatterUtilities {

    public static Icon icon = ImageUtilities.image2Icon(ImageUtilities.loadImage("org/netbeans/eclipse/formatter/icon.png"));
    
    static EclipseFormatter ef;
    static Preferences globalPrefs;
    
    public static EclipseFormatter getEclipseFormatter() {
        if (ef == null) {
            ef = new EclipseFormatter();
        }
        return ef;
    }

    public static Preferences getGlobalPrefs() {
        if (globalPrefs == null) {
            globalPrefs = NbPreferences.forModule(EclipseFormatterPanel.class);;
        }
        return globalPrefs;
    }
    
    public void reFormatWithEclipse(StyledDocument document, EclipseFormatter formatter, boolean isJava) {
        int caret = -1;
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor != null) {
            caret = editor.getCaretPosition();
        }
        final int length = document.getLength();
        String result = null;
        try {
            if (isJava) {
                String docText = null;
                try {
                    docText = document.getText(0, length);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                result = formatter.forCode(docText);
            }
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

    public boolean isJava(StyledDocument document) {
        boolean result = false;
        TokenHierarchy th = TokenHierarchy.get(document);
        Set<LanguagePath> languagePathSet = Collections.emptySet();
        if (document instanceof AbstractDocument) {
            AbstractDocument adoc = (AbstractDocument) document;
            adoc.readLock();
            try {
                languagePathSet = th.languagePaths();
                List<LanguagePath> languagePaths = new ArrayList<LanguagePath>(languagePathSet);
                for (LanguagePath lp : languagePaths) {
                    if (lp.mimePath().endsWith("text/x-java")) {
                        result = true;
                        break;
                    }
                }
            } finally {
                adoc.readUnlock();
            }
        }
        return result;
    }

}
