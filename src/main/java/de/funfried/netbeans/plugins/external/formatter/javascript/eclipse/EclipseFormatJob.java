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
package de.funfried.netbeans.plugins.external.formatter.javascript.eclipse;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.eclipse.AbstractEclipseFormatJob;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Eclipse formatter implementation of the {@link AbstractEclipseFormatJob} to
 * format a given document using the {@link EclipseJavascriptFormatterWrapper}.
 *
 * @author markiewb
 * @author bahlef
 */
class EclipseFormatJob extends AbstractEclipseFormatJob {
	/** The {@link EclipseJavascriptFormatterWrapper} implementation. */
	private final EclipseJavascriptFormatterWrapper formatter;

	/**
	 * Package private constructor to create a new instance of {@link EclipseFormatJob}.
	 *
	 * @param document       the {@link StyledDocument} which sould be formatted
	 * @param formatter      the {@link EclipseJavascriptFormatterWrapper} to use
	 * @param changedElement an optional range as a {@link Pair} object defining the offsets which should be formatted
	 */
	EclipseFormatJob(StyledDocument document, EclipseJavascriptFormatterWrapper formatter, Pair<Integer, Integer> changedElement) {
		super(document, singletonSortedSet(changedElement));

		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getFormattedContent(Preferences pref, String formatterFile, String formatterProfile, String code)
			throws ConfigReadException, ProfileNotFoundException, CannotLoadConfigurationException, FormattingFailedException {
		return formatter.format(formatterFile, formatterProfile, code, getLineFeed(pref), changedElements != null ? changedElements.first() : null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getFormatterFile(Preferences pref) {
		return EclipseJavascriptFormatterSettings.getEclipseFormatterFile(pref, document);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getFormatterProfile(Preferences pref) {
		return pref.get(EclipseJavascriptFormatterSettings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getLineFeed(Preferences pref) {
		String lineFeedSetting = pref.get(EclipseJavascriptFormatterSettings.LINEFEED, "");
		return Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getNotificationMessageForEclipseFormatterConfigurationFileType(String formatterFile, String formatterProfile) {
		String msg = "";
		if (EclipseJavascriptFormatterSettings.isWorkspaceMechanicFile(formatterFile)) {
			//Workspace mechanic file
			msg = String.format("Using %s", formatterFile);
		} else if (EclipseJavascriptFormatterSettings.isXMLConfigurationFile(formatterFile)) {
			//XML file
			msg = String.format("Using profile '%s' from %s", formatterProfile, formatterFile);
		} else if (EclipseJavascriptFormatterSettings.isProjectSetting(formatterFile)) {
			//org.eclipse.jdt.core.prefs
			msg = String.format("Using %s", formatterFile);
		}

		return msg;
	}

	/**
	 * Creates and returns a {@link SortedSet} within the given {@link Pair} of {@link Integer}s or {@code null}
	 * if the given {@link Pair} was {@code null}.
	 *
	 * @param pair the {@link Pair} which should be added to the {@link SortedSet}
	 *
	 * @return a {@link SortedSet} within the given {@link Pair} of {@link Integer}s or {@code null}
	 *         if the given {@link Pair} was {@code null}
	 */
	private static SortedSet<Pair<Integer, Integer>> singletonSortedSet(Pair<Integer, Integer> pair) {
		if (pair == null) {
			return null;
		}

		SortedSet<Pair<Integer, Integer>> sortedSet = new TreeSet<>();
		sortedSet.add(pair);

		return sortedSet;
	}
}
