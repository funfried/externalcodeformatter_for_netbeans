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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.StyledDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

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
public class ReformatWithEclipseAction extends CookieAction implements ActionListener {

    private EditorCookie context = null;

    @Override
    protected boolean enable(Node[] nodes) {
        if (nodes.length == 1) {
            final EditorCookie editorCookie = nodes[0].getLookup().lookup(EditorCookie.class);
            if (editorCookie != null && null != editorCookie.getDocument()) {
                boolean isJava = EclipseFormatterUtilities.isJava(editorCookie.getDocument());
                if (isJava) {
                    context = editorCookie;
                    return true;
                }
            }
        }
        context = null;
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final EditorCookie editorCookie = context;
        if (null == editorCookie || null == editorCookie.getDocument()) {
            return;
        }
        final StyledDocument styledDoc = editorCookie.getDocument();
        
        new FormatJavaAction().format(styledDoc, false);
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    //Not used:
    @Override
    public String getName() {
        return null;
    }

    //Not used:
    @Override
    protected void performAction(Node[] nodes) {
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
