/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 */
package de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse;

import de.markiewb.netbeans.plugins.eclipse.formatter.Pair;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.IFormatterStrategy;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author markiewb
 */
public class EclipseFormatterStrategy implements IFormatterStrategy {

    /**
     *
     * @param document
     * @param formatter
     * @param forSave true, if invoked by save action
     */
    @Override
    public void format(final StyledDocument document, final EclipseFormatter formatter, boolean forSave, final boolean preserveBreakpoints, final SortedSet<Pair> changedElements) {
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
        try {
            final EclipseFormatterRunnable formatterRunnable = new EclipseFormatterRunnable(document, formatter, _dot, _mark, preserveBreakpoints, _caret, editor, changedElements);
            formatterRunnable.run();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //Set caret after the formatting, if possible
                    if (editor != null) {
                        editor.setCaretPosition(Math.max(0, Math.min(_caret, editor.getDocument().getLength())));
                    }
                }
            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

}
