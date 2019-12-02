/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.netbeans;

import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.Utils;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author markiewb
 */
class NetBeansFormatterRunnable implements Runnable {
	private final StyledDocument document;

	private final Reformat rf;

	private final int startOffset;

	private final int endOffset;

	NetBeansFormatterRunnable(StyledDocument document, Reformat rf, int dot, int mark) {
		this.document = document;
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

			Preferences pref = Settings.getActivePreferences(document);
			if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
				String detail = getNotificationMessageForNetBeans(Utils.isJava(document));

				NotificationDisplayer.getDefault().notify("Format using NetBeans formatter", Icons.ICON_NETBEANS, detail, null);
			}

			StatusDisplayer.getDefault().setStatusText("Format using NetBeans formatter");
		} catch (BadLocationException ex) {
		}
	}

	private String getNotificationMessageForNetBeans(boolean isJava) {
		String detail = "";
		if (!isJava) {
			detail += "Because file isn't a Java file. ";
		}
		return detail;
	}
}
