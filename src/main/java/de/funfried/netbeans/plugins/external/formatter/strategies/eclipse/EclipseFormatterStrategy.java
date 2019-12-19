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

import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractJavaFormatterStrategy;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class EclipseFormatterStrategy extends AbstractJavaFormatterStrategy {
	private final EclipseFormatter formatter = new EclipseFormatter();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void format(StyledDocument document, int dot, int mark, SortedSet<Pair<Integer, Integer>> changedElements) {
		EclipseFormatterRunnable formatterRunnable = new EclipseFormatterRunnable(document, formatter, dot, mark, changedElements);
		formatterRunnable.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isActivated(StyledDocument document) {
		Preferences pref = Settings.getActivePreferences(document);
		return pref.getBoolean(Settings.ECLIPSE_FORMATTER_ENABLED, false);
	}
}
