/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * bahlef
 */
package de.funfried.netbeans.plugins.external.formatter.java.eclipse;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.Utils;
import de.funfried.netbeans.plugins.external.formatter.base.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Eclipse formatter implementation of the {@link AbstractFormatJob} to
 * format a given document using the {@link EclipseJavaFormatterWrapper}.
 *
 * @author markiewb
 * @author bahlef
 */
class EclipseFormatJob extends AbstractFormatJob {
	/** * The {@link EclipseJavaFormatterWrapper} implementation. */
	private final EclipseJavaFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link EclipseFormatJob}.
	 *
	 * @param document        the {@link StyledDocument} which sould be formatted
	 * @param formatter       the {@link EclipseJavaFormatterWrapper} to use
	 * @param changedElements the ranges which should be formatted
	 */
	EclipseFormatJob(StyledDocument document, EclipseJavaFormatterWrapper formatter, SortedSet<Pair<Integer, Integer>> changedElements) {
		super(document, changedElements);

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format() throws BadLocationException {
		boolean isJava = Utils.isJava(document);
		if (!isJava) {
			throw new FileTypeNotSupportedException("The file type '" + NbEditorUtilities.getMimeType(document) + "' is not supported by the Eclipse Java Code Formatter");
		}

		Preferences pref = Settings.getActivePreferences(document);

		String formatterFile = EclipseJavaFormatterSettings.getEclipseFormatterFile(pref, document);
		String formatterProfile = pref.get(EclipseJavaFormatterSettings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
		String sourceLevel = pref.get(EclipseJavaFormatterSettings.SOURCELEVEL, "");
		String lineFeedSetting = pref.get(EclipseJavaFormatterSettings.LINEFEED, "");
		String lineFeed = EclipseJavaFormatterSettings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));

		//save with configured linefeed
		if (null != lineFeed) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		String code = getCode();

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
		if (EclipseJavaFormatterSettings.isWorkspaceMechanicFile(formatterFile)) {
			//Workspace mechanic file
			msg = String.format("Using %s", formatterFile);
		} else if (EclipseJavaFormatterSettings.isXMLConfigurationFile(formatterFile)) {
			//XML file
			msg = String.format("Using profile '%s' from %s", formatterProfile, formatterFile);
		} else if (EclipseJavaFormatterSettings.isProjectSetting(formatterFile)) {
			//org.eclipse.jdt.core.prefs
			msg = String.format("Using %s", formatterFile);
		}

		return msg;
	}
}
