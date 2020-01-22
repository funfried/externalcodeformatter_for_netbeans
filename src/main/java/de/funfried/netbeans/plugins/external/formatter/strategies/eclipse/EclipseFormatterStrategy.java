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

import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.apache.commons.lang3.tuple.Pair;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractJavaFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.strategies.IFormatterStrategyService;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Eclipse implementation of the {@link AbstractJavaFormatterStrategy}.
 *
 * @author markiewb
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Eclipse Java Code Formatter"
})
@ServiceProvider(service = IFormatterStrategyService.class)
public class EclipseFormatterStrategy extends AbstractJavaFormatterStrategy {
	/** The ID of this formatter strategy. */
	public static final String ID = "eclipse-java-formatter";

	/** The {@link EclipseFormatter} implementation. */
	private final EclipseFormatter formatter = new EclipseFormatter();

	/**
	 * {@inheritDoc}
	 */
	@NotNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(EclipseFormatterStrategy.class, "FormatterName");
	}

	/**
	 * {@inheritDoc}
	 */
	@NotNull
	@Override
	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Null
	@Override
	public Integer getContinuationIndentSize(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.continuation_indentation");
			if (value != null) {
				ret = Integer.valueOf(value);
			}
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Null
	@Override
	public Integer getIndentSize(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.indentation.size");
			if (value != null) {
				ret = Integer.valueOf(value);
			}
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Null
	@Override
	public Integer getRightMargin(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		String value = getEclipseFormatterProperty(null, document, "org.eclipse.jdt.core.formatter.lineSplit");
		if (value != null) {
			ret = Integer.valueOf(value);
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Runnable getRunnable(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
		return new EclipseFormatterRunnable(document, formatter, changedElements);
	}

	/**
	 * {@inheritDoc}
	 */
	@Null
	@Override
	public Integer getSpacesPerTab(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			if (preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, true)) {
				ret = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);
			} else {
				String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.tabulation.size");
				if (value != null) {
					ret = Integer.valueOf(value);
				}
			}
		}

		return ret;
	}

	/**
	 * Reads the configuration value of the Eclipse formatter configuration from a given
	 * {@link Document} for the given {@code  key}.
	 *
	 * @param preferences the {@link Preferences} of the {@link Document} if already loaded
	 *                    or {@code null} to read the preferences of the given {@link Document}
	 * @param document    the {@link Document} where to read the value from
	 * @param key         the key of the value which should be read
	 *
	 * @return the configuration value of the Eclipse formatter configuration from a given
	 *         {@link Document} for the given {@code  key}
	 */
	private String getEclipseFormatterProperty(Preferences preferences, Document document, String key) {
		if (document == null) {
			return null;
		}

		if (preferences == null) {
			preferences = Settings.getActivePreferences(document);
		}

		String formatterFile = Settings.getEclipseFormatterFile(preferences, document);
		String formatterProfile = preferences.get(Settings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
		String sourceLevel = preferences.get(Settings.SOURCELEVEL, "");

		Map<String, String> config = EclipseFormatterConfig.parseConfig(formatterFile, formatterProfile, sourceLevel);

		return config.getOrDefault(key, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Null
	@Override
	public Boolean isExpandTabToSpaces(Document document) {
		if (document == null) {
			return null;
		}

		Boolean ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.tabulation.char");
			if (value != null) {
				ret = Objects.equals(value, "space");
			}
		}

		return ret;
	}

	/**
	 * Returns {@code true} if using the formatter indentation settings from the external
	 * formatter is activated, otherwise {@code false}.
	 *
	 * @param prefs the {@link Preferences} where to check
	 *
	 * @return {@code true} if using the formatter indentation settings from the external
	 *         formatter is activated, otherwise {@code false}
	 */
	private boolean isUseFormatterIndentationSettings(Preferences prefs) {
		return prefs.getBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
	}
}
