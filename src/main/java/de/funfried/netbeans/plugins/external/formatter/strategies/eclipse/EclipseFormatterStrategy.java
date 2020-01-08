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

import javax.annotation.Nullable;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractJavaFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.strategies.IFormatterStrategyService;
import de.funfried.netbeans.plugins.external.formatter.strategies.netbeans.NetBeansFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author markiewb
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Eclipse Java Code Formatter"
})
@ServiceProvider(service = IFormatterStrategyService.class)
public class EclipseFormatterStrategy extends AbstractJavaFormatterStrategy {
	public static final String ID = "eclipse-java-formatter";

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
	@Nullable
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
	@Nullable
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
	@Nullable
	@Override
	public Integer getRightMargin(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.jdt.core.formatter.lineSplit");
		if (value != null) {
			ret = Integer.valueOf(value);
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
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

	private String getEclipseFormatterProperty(Preferences preferences, Document document, String key) {
		if (preferences == null || document == null) {
			return null;
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
	@Nullable
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

	private boolean isUseFormatterIndentationSettings(Preferences prefs) {
		String enabledFormatter = prefs.get(Settings.ENABLED_FORMATTER, NetBeansFormatterStrategy.ID);
		if (ID.equals(enabledFormatter)) {
			return prefs.getBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		}

		return false;
	}
}
