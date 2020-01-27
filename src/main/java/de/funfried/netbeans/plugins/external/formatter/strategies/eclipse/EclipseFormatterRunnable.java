/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.Utils;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractFormatterRunnable;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Eclipse formatter implementation of the {@link AbstractFormatterRunnable} to
 * format a given document using the {@link EclipseFormatter}.
 *
 * @author markiewb
 * @author bahlef
 */
class EclipseFormatterRunnable extends AbstractFormatterRunnable {
	/** The {@link EclipseFormatter} implementation. */
	private final EclipseFormatter formatter;

	/**
	 * Package private constructor to create a new instance of {@link EclipseFormatterRunnable}.
	 *
	 * @param document        the {@link StyledDocument} which sould be formatted
	 * @param formatter       the {@link EclipseFormatter} to use
	 * @param changedElements the ranges which should be formatted
	 */
	EclipseFormatterRunnable(StyledDocument document, EclipseFormatter formatter, SortedSet<Pair<Integer, Integer>> changedElements) {
		super(document, changedElements);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		boolean isJava = Utils.isJava(document);
		if (!isJava) {
			throw new FileTypeNotSupportedException("The file type '" + NbEditorUtilities.getMimeType(document) + "' is not supported by the Eclipse Java Code Formatter");
		}

		Preferences pref = Settings.getActivePreferences(document);

		String formatterFile = Settings.getEclipseFormatterFile(pref, document);
		String formatterProfile = pref.get(Settings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
		String sourceLevel = pref.get(Settings.SOURCELEVEL, "");
		String lineFeedSetting = pref.get(Settings.LINEFEED, "");
		String lineFeed = Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));

		String code = getCode(lineFeedSetting);

		try {
			SortedSet<Pair<Integer, Integer>> regions = getFormatableSections(code);

			String formattedContent = formatter.format(formatterFile, formatterProfile, code, lineFeed, sourceLevel, regions);
			if (setFormattedCode(code, formattedContent)) {
				String msg = getNotificationMessageForEclipseFormatterConfigurationFileType(formatterFile, formatterProfile);

				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Eclipse formatter", Icons.ICON_ECLIPSE, msg, null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using Eclipse formatter: " + msg);
				});
			}
		} catch (ProfileNotFoundException ex) {
			SwingUtilities.invokeLater(() -> {
				NotifyDescriptor notify = new NotifyDescriptor.Message(
						String.format("<html>Profile '%s' not found in <tt>%s</tt><br><br>Please configure a valid one in the project properties OR at Tools|Options|Java|Eclipse Formatter!", formatterProfile,
								formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);
			});

			throw ex;
		} catch (CannotLoadConfigurationException ex) {
			SwingUtilities.invokeLater(() -> {
				NotifyDescriptor notify = new NotifyDescriptor.Message(String.format("<html>Could not find configuration file %s.<br>Make sure the file exists and it can be read.", formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);
			});

			throw ex;
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex);
		} catch (RuntimeException ex) {
			throw ex;
		}
	}

	/**
	 * Returns the message which should be shown in a notification after the formatting is done.
	 *
	 * @param formatterFile    the used formatter configuration file
	 * @param formatterProfile the used formatter profile
	 *
	 * @return the message which should be shown in a notification after the formatting is done
	 */
	private String getNotificationMessageForEclipseFormatterConfigurationFileType(String formatterFile, String formatterProfile) {
		String msg = "";
		if (Settings.isWorkspaceMechanicFile(formatterFile)) {
			//Workspace mechanic file
			msg = String.format("Using %s", formatterFile);
		} else if (Settings.isXMLConfigurationFile(formatterFile)) {
			//XML file
			msg = String.format("Using profile '%s' from %s", formatterProfile, formatterFile);
		} else if (Settings.isProjectSetting(formatterFile)) {
			//org.eclipse.jdt.core.prefs
			msg = String.format("Using %s", formatterFile);
		}

		return msg;
	}
}
