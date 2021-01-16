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

import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.NonNull;

import de.funfried.netbeans.plugins.external.formatter.eclipse.AbstractEclipseFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Eclipse formatter implementation of the {@link AbstractEclipseFormatJob} to
 * format a given document using the {@link EclipseJavaFormatterWrapper}.
 *
 * @author markiewb
 * @author bahlef
 */
class EclipseFormatJob extends AbstractEclipseFormatJob {
	/** * The {@link EclipseJavaFormatterWrapper} implementation. */
	private final EclipseJavaFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link EclipseFormatJob}.
	 *
	 * @param document the {@link StyledDocument} which sould be formatted
	 * @param formatter the {@link EclipseJavaFormatterWrapper} to use
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
	protected String getFormattedContent(Preferences pref, String formatterFile, String formatterProfile, String code)
			throws ConfigReadException, ProfileNotFoundException, CannotLoadConfigurationException, FormattingFailedException {
		String sourceLevel = pref.get(EclipseJavaFormatterSettings.SOURCELEVEL, "");

		SortedSet<Pair<Integer, Integer>> regions = getFormatableSections(code);

		return formatter.format(formatterFile, formatterProfile, code, getLineFeed(pref), sourceLevel, regions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getFormatterFile(Preferences pref) {
		return EclipseJavaFormatterSettings.getEclipseFormatterFile(pref, document);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getFormatterProfile(Preferences pref) {
		return pref.get(EclipseJavaFormatterSettings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getLineFeed(Preferences pref) {
		String lineFeedSetting = pref.get(EclipseJavaFormatterSettings.LINEFEED, "");
		return Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
	}

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	protected String getNotificationMessageForEclipseFormatterConfigurationFileType(String formatterFile, String formatterProfile) {
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
