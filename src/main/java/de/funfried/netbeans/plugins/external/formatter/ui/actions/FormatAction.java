/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * Saad Mufti <saad.mufti@teamaol.com>
 */
package de.funfried.netbeans.plugins.external.formatter.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.netbeans.api.editor.EditorActionRegistration;
import org.netbeans.editor.BaseAction;
import org.openide.util.NbBundle;

import de.funfried.netbeans.plugins.external.formatter.strategies.FormatterAdvice;
import de.funfried.netbeans.plugins.external.formatter.strategies.FormatterStrategyDispatcher;

/**
 * Registration analog to
 * http://hg.netbeans.org/jet-main/file/01c13d4da2da/java.hints/src/org/netbeans/modules/java/hints/OrganizeMembers.java
 *
 * @author markiewb
 * @author bahlef
 */
@NbBundle.Messages({ "external-format=Format" })
@EditorActionRegistration(category = "Source", name = FormatAction.MACRONAME, menuPath = "Source", menuPosition = 299, menuText = "#external-format", popupPath = "", popupPosition = 1599, popupText = "#external-format")
public class FormatAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	public static final String MACRONAME = "external-format";

	@Override
	public void actionPerformed(ActionEvent e, JTextComponent component) {
		if (component == null || !component.isEditable() || !component.isEnabled()) {
			return;
		}

		int start = component.getSelectionStart();
		int end = component.getSelectionEnd();
		int caret = component.getCaretPosition();

		final StyledDocument document = (StyledDocument) component.getDocument();

		FormatterStrategyDispatcher.getInstance().format(new FormatterAdvice(document, start, end, caret, component));
	}
}
