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
package de.funfried.netbeans.plugins.external.formatter.json.jackson;

import java.util.prefs.Preferences;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.json.base.AbstractJsonFormatterService;
import de.funfried.netbeans.plugins.external.formatter.json.jackson.ui.JacksonJsonFormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Jackson Json implementation of the {@link AbstractJsonFormatterService}.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Jackson Json Code Formatter"
})
@ServiceProvider(service = FormatterService.class, position = 500)
public class JacksonJsonFormatterService extends AbstractJsonFormatterService {
	/** The ID of this formatter service. */
	public static final String ID = "jackson-json-formatter";

	/** * The {@link JacksonJsonFormatterWrapper} implementation. */
	private final JacksonJsonFormatterWrapper formatter = new JacksonJsonFormatterWrapper();

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(JacksonJsonFormatterService.class, "FormatterName");
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
	public FormatterOptionsPanel getOptionsPanel() {
		return new JacksonJsonFormatterOptionsPanel();
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
			ret = preferences.getInt(JacksonJsonFormatterSettings.INDENT_SIZE, 2);
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
			ret = preferences.getInt(JacksonJsonFormatterSettings.INDENT_SIZE, 2);
		}

		return ret;
	}

	/**
	 * Not supported by this formatter returns always {@code 0} except the given
	 * {@link Document} is {@code null}, then also {@code null will be returned}.
	 *
	 * @param document the {@link Document} for which the right margin is requested
	 *
	 * @return {@code 0} except the given {@link Document} is {@code null}, then
	 *         also {@code null will be returned}
	 */
	@CheckForNull
	@Override
	public Integer getRightMargin(Document document) {
		if (document == null) {
			return null;
		}

		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FormatJob getFormatJob(StyledDocument document) {
		return new JacksonFormatJob(document, formatter);
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
			if (preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, true)) {
				ret = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);
			} else {
				ret = preferences.getInt(JacksonJsonFormatterSettings.SPACES_PER_TAB, 2);
			}
		}

		return ret;
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

		Boolean ret = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			ret = preferences.getBoolean(JacksonJsonFormatterSettings.EXPAND_TABS_TO_SPACES, true);
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
