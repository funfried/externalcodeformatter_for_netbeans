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
package de.markiewb.netbeans.plugins.eclipse.formatter.strategies.netbeans;

import de.markiewb.netbeans.plugins.eclipse.formatter.Pair;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.IFormatterStrategy;
import java.util.List;
import java.util.SortedSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;


/**
 *
 * @author markiewb
 */
public class NetBeansFormatterStrategy implements IFormatterStrategy {
    
    /**
     *
     * @param document
     * @param forSave true, if invoked by save action
     */
    @Override
    public void format(final StyledDocument document, final EclipseFormatter formatter, boolean forSave, final boolean preserveBreakpoints, SortedSet<Pair> changedElements) {
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
            NbDocument.runAtomicAsUser(document, new NetBeansFormatterRunnable(document, rf, dot, mark));
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            rf.unlock();
        }
    }
    
}
