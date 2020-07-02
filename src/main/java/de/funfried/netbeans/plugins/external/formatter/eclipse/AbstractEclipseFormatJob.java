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
package de.funfried.netbeans.plugins.external.formatter.eclipse;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.editor.BaseDocument;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.AbstractFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Abstract Eclipse formatter implementation of the {@link AbstractFormatJob} as a base
 * class for any type of Eclipse Formatter.
 *
 * @author bahlef
 */
public abstract class AbstractEclipseFormatJob extends AbstractFormatJob {
	/**
	 * Protected constructor to create a new instance of {@link AbstractEclipseFormatJob}.
	 *
	 * @param document        the {@link StyledDocument} which sould be formatted
	 * @param changedElements the ranges which should be formatted
	 */
	protected AbstractEclipseFormatJob(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
		super(document, changedElements);
	}

	/**
	 * Returns the formatted content.
	 *
	 * @param pref             the {@link Preferences}
	 * @param formatterFile    the path to the formatter configuration file
	 * @param formatterProfile the name of the formatter configuration profile
	 * @param code             the current (unformatted) code
	 *
	 * @return the formatted content
	 *
	 * @throws ConfigReadException              if there is an issue parsing the formatter configuration
	 * @throws ProfileNotFoundException         if the given {@code profile} could not be found
	 * @throws CannotLoadConfigurationException if there is any issue accessing or reading the formatter configuration
	 * @throws FormattingFailedException        if the external formatter failed to format the given code
	 */
	protected abstract String getFormattedContent(Preferences pref, String formatterFile, String formatterProfile, String code)
			throws ConfigReadException, ProfileNotFoundException, CannotLoadConfigurationException, FormattingFailedException;

	/**
	 * Returns the configured formatter configuration file.
	 *
	 * @param pref the {@link Preferences}
	 *
	 * @return the configured formatter configuration file
	 */
	protected abstract String getFormatterFile(Preferences pref);

	/**
	 * Returns the configured formatter profile name.
	 *
	 * @param pref the {@link Preferences}
	 *
	 * @return the configured formatter profile name
	 */
	protected abstract String getFormatterProfile(Preferences pref);

	/**
	 * Returns the configured line feed.
	 *
	 * @param pref the {@link Preferences}
	 *
	 * @return the configured line feed
	 */
	protected abstract String getLineFeed(Preferences pref);

	/**
	 * Returns the content of the {@code document}.
	 *
	 * @param pref the {@link Preferences}
	 *
	 * @return The content of the {@code document}
	 */
	protected String getCode(Preferences pref) {
		String lineFeed = getLineFeed(pref);

		//save with configured linefeed
		if (null != lineFeed) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		return getCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void format() throws BadLocationException {
		Preferences pref = Settings.getActivePreferences(document);

		String formatterFile = getFormatterFile(pref);
		String formatterProfile = getFormatterProfile(pref);

		String code = getCode(pref);

		try {
			String formattedContent = getFormattedContent(pref, formatterFile, formatterProfile, code);
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
						String.format("<html>Profile '%s' not found in <tt>%s</tt><br><br>Please configure a valid one in the project properties OR at Tools|Options|Editor|External Formatter!", formatterProfile,
								formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);

				StatusDisplayer.getDefault().setStatusText(String.format("Profile '%s' not found in %s", formatterProfile, formatterFile));
			});

			throw ex;
		} catch (CannotLoadConfigurationException ex) {
			SwingUtilities.invokeLater(() -> {
				NotifyDescriptor notify = new NotifyDescriptor.Message(String.format("<html>Could not find configuration file %s.<br>Make sure the file exists and it can be read.", formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);

				StatusDisplayer.getDefault().setStatusText(String.format("Could not find configuration file %s. Make sure the file exists and it can be read.", formatterFile));
			});

			throw ex;
		} catch (FormattingFailedException ex) {
			SwingUtilities.invokeLater(() -> {
				StatusDisplayer.getDefault().setStatusText("Failed to format using Eclipse formatter: " + ex.getMessage());
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
	protected abstract String getNotificationMessageForEclipseFormatterConfigurationFileType(String formatterFile, String formatterProfile);
}
