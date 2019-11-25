/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.eclipse.formatter.strategies.netbeans;

import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.eclipse.formatter.Utils;
import de.funfried.netbeans.plugins.eclipse.formatter.options.Settings;

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
				GuardedSectionManager guards = GuardedSectionManager.getInstance(document);
				final boolean hasGuardedSections = guards != null;

				String detail = getNotificationMessageForNetBeans(hasGuardedSections, pref.getBoolean(Settings.ECLIPSE_FORMATTER_ENABLED, false), Utils.isJava(document));

				NotificationDisplayer.getDefault().notify("Format using NetBeans formatter", Utils.iconNetBeans, detail, null);
			}

			StatusDisplayer.getDefault().setStatusText("Format using NetBeans formatter");
		} catch (BadLocationException ex) {
		}
	}

	private String getNotificationMessageForNetBeans(final boolean hasGuardedSections, final boolean isEclipseFormatterEnabled, final boolean isJava) {
		String detail = "";
		if (hasGuardedSections && isEclipseFormatterEnabled) {
			detail += "Because file contains guarded sections. ";
		}
		if (!isJava) {
			detail += "Because file isn't a Java file. ";
		}
		return detail;
	}
}
