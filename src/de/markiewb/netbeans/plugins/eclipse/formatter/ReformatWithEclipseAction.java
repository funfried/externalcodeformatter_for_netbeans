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
package de.markiewb.netbeans.plugins.eclipse.formatter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.StyledDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
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
@NbBundle.Messages("CTL_EclipseFormatter=Format with Eclipse formatter")
public class ReformatWithEclipseAction implements ActionListener {

    private static final Logger LOG = Logger.getLogger(ReformatWithEclipseAction.class.getName());

    private EditorCookie context = null;

    public ReformatWithEclipseAction(EditorCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (null == context || null == context.getDocument()) {
            return;
        }
        final StyledDocument document = context.getDocument();
        boolean isJava = EclipseFormatterUtilities.isJava(document);
        if (!isJava) {
            return;
        }

        final StyledDocument styledDoc = document;
        final boolean noSaveAction = false;
        saveDocumentIfModified();
        new FormatJavaAction().format(styledDoc, noSaveAction);
        saveDocumentIfModified();
    }

    private void saveDocumentIfModified() {
        if (context.isModified()) {
            try {
                context.saveDocument();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Document {0} could not be saved", new Object[]{context.getDocument(), ex.getMessage()});
            }
        }
    }

}
