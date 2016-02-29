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

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.editor.indent.api.Reformat;

/**
 *
 * @author markiewb
 */
class NetBeansFormatterRunnable implements Runnable {
    
    private final Reformat rf;
    private final int startOffset;
    private final int endOffset;

    NetBeansFormatterRunnable(StyledDocument document, Reformat rf, int dot, int mark) {
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
