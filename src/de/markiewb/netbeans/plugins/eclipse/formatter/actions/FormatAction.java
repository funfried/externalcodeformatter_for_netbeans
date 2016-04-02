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
package de.markiewb.netbeans.plugins.eclipse.formatter.actions;

import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.ParameterObject;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.FormatterStrategyDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.text.StyledDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.text.NbDocument;
import org.openide.util.NbBundle;

@ActionID(
        category = "Source",
        id = "org.netbeans.eclipse.formatter.ReformatWithEclipseBeforeSaveTask")
@ActionRegistration(
        lazy = true,
        displayName = "#CTL_EclipseFormatter")
@ActionReferences({
    @ActionReference(path = "Menu/Source", position = 0)
})
@NbBundle.Messages("CTL_EclipseFormatter=Format with Eclipse formatter 4.4")
public class FormatAction implements ActionListener {

    private static final Logger LOG = Logger.getLogger(FormatAction.class.getName());

    private EditorCookie context = null;

    public FormatAction(EditorCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (null == context || null == context.getDocument()) {
            return;
        }
        JEditorPane editor = NbDocument.findRecentEditorPane(context);
        int start = (editor != null) ? editor.getSelectionStart() : -1;
        int end = (editor != null) ? editor.getSelectionEnd() : -1;
        int caret = (editor != null) ? editor.getCaretPosition() : -1;

        final StyledDocument document = context.getDocument();
        
        ParameterObject po = new ParameterObject();
        po.styledDoc = document;
        po.changedElements = null;
        po.forSave = false;
        po.selectionStart = start;
        po.selectionEnd = end;
        po.caret = caret;
        po.editor = editor;
        
        new FormatterStrategyDispatcher().format(po);
    }

}
