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

import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.javascript.base.AbstractJavascriptFormatterService;
import de.funfried.netbeans.plugins.external.formatter.javascript.eclipse.ui.EclipseJavascriptFormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Eclipse implementation of the {@link AbstractJavascriptFormatterService}.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Eclipse Javascript Code Formatter"
})
@ServiceProvider(service = FormatterService.class, position = 1000)
public class EclipseJavascriptFormatterService extends AbstractJavascriptFormatterService {
	/** The ID of this formatter service. */
	public static final String ID = "eclipse-javascript-formatter";

	/** * The {@link EclipseJavascriptFormatterWrapper} implementation. */
	private final EclipseJavascriptFormatterWrapper formatter = new EclipseJavascriptFormatterWrapper();

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(EclipseJavascriptFormatterService.class, "FormatterName");
	}

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormatterOptionsPanel createOptionsPanel(Project project) {
		return new EclipseJavascriptFormatterOptionsPanel(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	@Override
	public Integer getContinuationIndentSize(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.wst.jsdt.core.formatter.continuation_indentation");
			if (value != null) {
				ret = Integer.valueOf(value);

				Integer indentSize = getIndentSize(document);
				if (indentSize != null) {
					ret *= indentSize;
				}
			}
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	@Override
	public Integer getIndentSize(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			String tabChar = getEclipseFormatterProperty(preferences, document, "org.eclipse.wst.jsdt.core.formatter.tabulation.char");
			if (Objects.equals(tabChar, "mixed")) {
				String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.wst.jsdt.core.formatter.indentation.size");
				if (value != null) {
					ret = Integer.valueOf(value);
				}
			} else {
				ret = getSpacesPerTab(document);
			}

		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	@Override
	public Integer getRightMargin(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		String value = getEclipseFormatterProperty(null, document, "org.eclipse.wst.jsdt.core.formatter.lineSplit");
		if (value != null) {
			ret = Integer.valueOf(value);
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FormatJob getFormatJob(StyledDocument document, Pair<Integer, Integer> changedElement) {
		return new EclipseFormatJob(document, formatter, changedElement);
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	@Override
	public Integer getSpacesPerTab(Document document) {
		if (document == null) {
			return null;
		}

		Integer ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			if (!isExpandTabToSpaces(document, preferences) && preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, true)) {
				ret = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);
			} else {
				String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.wst.jsdt.core.formatter.tabulation.size");
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
	 *        or {@code null} to read the preferences of the given {@link Document}
	 * @param document the {@link Document} where to read the value from
	 * @param key the key of the value which should be read
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

		String formatterFile = EclipseJavascriptFormatterSettings.getEclipseFormatterFile(preferences, document);
		String formatterProfile = preferences.get(EclipseJavascriptFormatterSettings.ACTIVE_PROFILE, "");

		Map<String, String> config = EclipseFormatterConfig.parseConfig(formatterFile, formatterProfile);

		return config.getOrDefault(key, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	@Override
	public Boolean isExpandTabToSpaces(Document document) {
		if (document == null) {
			return null;
		}

		return isExpandTabToSpaces(document, Settings.getActivePreferences(document));
	}

	private Boolean isExpandTabToSpaces(Document document, Preferences preferences) {
		if (document == null || preferences == null) {
			return null;
		}

		Boolean ret = null;

		if (isUseFormatterIndentationSettings(preferences)) {
			String value = getEclipseFormatterProperty(preferences, document, "org.eclipse.wst.jsdt.core.formatter.tabulation.char");
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CheckForNull
	public Boolean organizeImports(StyledDocument document, boolean afterFixImports) throws BadLocationException {
		return null;
	}
}
