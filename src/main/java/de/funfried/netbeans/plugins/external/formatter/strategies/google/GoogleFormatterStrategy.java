/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.google;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractFormatterStrategy;

/**
 *
 * @author bahlef
 */
public class GoogleFormatterStrategy extends AbstractFormatterStrategy {
	private final GoogleFormatter formatter = new GoogleFormatter();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void format(StyledDocument document, int dot, int mark, SortedSet<Pair<Integer, Integer>> changedElements) {
		GoogleFormatterRunnable formatterRunnable = new GoogleFormatterRunnable(document, formatter, dot, mark, changedElements);
		formatterRunnable.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isActivated(StyledDocument document) {
		Preferences pref = Settings.getActivePreferences(document);
		return pref.getBoolean(Settings.GOOGLE_FORMATTER_ENABLED, false);
	}
}
