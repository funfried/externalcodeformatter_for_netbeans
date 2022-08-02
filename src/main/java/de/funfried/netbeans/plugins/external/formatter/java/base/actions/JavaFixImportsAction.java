/*
 * Copyright (c) 2022 fbahle.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * fbahle - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter.java.base.actions;

import java.awt.event.ActionEvent;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.netbeans.api.editor.EditorActionRegistration;
import org.netbeans.editor.BaseAction;
import org.netbeans.editor.ext.ExtKit;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

import de.funfried.netbeans.plugins.external.formatter.FormatterServiceDelegate;
import de.funfried.netbeans.plugins.external.formatter.ui.editor.EditorUtils;

/**
 * {@link BaseAction} for fixing imports which overrides the NetBeans original action.
 *
 * @author fbahle
 */
@EditorActionRegistration(name = "fix-imports", mimeType = "text/x-java", shortDescription = "#desc-fix-imports", popupText = "#popup-fix-imports")
public class JavaFixImportsAction extends BaseAction {
	private static final long serialVersionUID = -3969332137881749109L;

	public JavaFixImportsAction() {
		super(BaseAction.ABBREV_RESET | BaseAction.MAGIC_POSITION_RESET | BaseAction.UNDO_MERGE_RESET);
		putValue(ExtKit.TRIMMED_TEXT, NbBundle.getMessage(JavaFixImportsAction.class, "fix-imports-trimmed"));
		putValue(BaseAction.SHORT_DESCRIPTION, NbBundle.getMessage(JavaFixImportsAction.class, "desc-fix-imports")); // NOI18N
		putValue(BaseAction.POPUP_MENU_TEXT, NbBundle.getMessage(JavaFixImportsAction.class, "popup-fix-imports")); // NOI18N
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent evt, JTextComponent target) {
		if (target != null) {
			Document document = target.getDocument();
			Lookup lookup = Lookups.forPath("extFormatters/backupOrgActions/fixImports");
			BaseAction action = lookup.lookup(BaseAction.class);
			action.actionPerformed(evt, target);

			FormatterServiceDelegate.getInstance().organizeImports(EditorUtils.toStyledDocument(document), true);
		}
	}
}
