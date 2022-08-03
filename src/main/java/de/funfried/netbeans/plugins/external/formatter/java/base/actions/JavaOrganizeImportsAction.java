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
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.netbeans.api.editor.EditorActionNames;
import org.netbeans.api.editor.EditorActionRegistration;
import org.netbeans.editor.BaseAction;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

import de.funfried.netbeans.plugins.external.formatter.FormatterServiceDelegate;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.editor.EditorUtils;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * {@link BaseAction} for organizing imports which overrides the NetBeans original action.
 *
 * @author fbahle
 */
@EditorActionRegistration(name = EditorActionNames.organizeImports, mimeType = "text/x-java", menuPath = "Source", menuPosition = 2430, menuText = "#" + EditorActionNames.organizeImports
		+ "_menu_text")
public class JavaOrganizeImportsAction extends BaseAction {
	private static final long serialVersionUID = 2459410245945401241L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent evt, JTextComponent target) {
		if (target != null) {
			Document document = target.getDocument();
			Boolean ret = FormatterServiceDelegate.getInstance().organizeImports(EditorUtils.toStyledDocument(document), false);
			if (!Boolean.TRUE.equals(ret)) {
				Lookup lookup = Lookups.forPath("extFormatters/backupOrgActions/organizeImports");
				BaseAction action = lookup.lookup(BaseAction.class);
				action.actionPerformed(evt, target);

				Preferences pref = Settings.getActivePreferences(document);

				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Organized imports using NetBeans internal logic", Icons.ICON_NETBEANS, "", null);
					}

					StatusDisplayer.getDefault().setStatusText("Organized imports using NetBeans internal logic");
				});
			}
		}
	}
}
