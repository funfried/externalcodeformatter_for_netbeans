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
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.ParameterObject;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.IFormatterStrategy;
import java.util.SortedSet;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
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
    public void format(EclipseFormatter formatter, boolean preserveBreakpoints, ParameterObject po) {
        final int selectionStart = po.selectionStart;
        final int selectionEnd = po.selectionEnd;
        final boolean forSave = po.forSave;
        final SortedSet<Pair> changedElements = po.changedElements;
        final StyledDocument document = po.styledDoc;
        final JTextComponent editor = po.editor;
        final int caret = po.caret;

        final int _dot = (!forSave) ? selectionStart : -1;
        final int _mark = (!forSave) ? selectionEnd : -1;
        final int _caret = caret;
        try {
            final EclipseFormatterRunnable formatterRunnable = new EclipseFormatterRunnable(document, formatter, _dot, _mark, preserveBreakpoints, changedElements);
            formatterRunnable.run();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //Set caret after the formatting, if possible
                    if (editor != null && _caret > 0) {
                        final int car = Math.max(0, Math.min(_caret, editor.getDocument().getLength()));
                        editor.setCaretPosition(car);
                        editor.requestFocus();
                        editor.requestFocusInWindow();
                    }
                }
            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

}
