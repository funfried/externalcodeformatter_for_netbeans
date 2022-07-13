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
package de.funfried.netbeans.plugins.external.formatter.xml.jsoup;

import java.util.prefs.Preferences;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import de.funfried.netbeans.plugins.external.formatter.xml.base.AbstractXmlFormatterService;
import de.funfried.netbeans.plugins.external.formatter.xml.jsoup.ui.JsoupXmlFormatterOptionsPanel;

/**
 * Jsoup XML implementation of the {@link AbstractXmlFormatterService}.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Jsoup XML Code Formatter"
})
@ServiceProvider(service = FormatterService.class, position = 1000)
public class JsoupXmlFormatterService extends AbstractXmlFormatterService {
	/** The ID of this formatter service. */
	public static final String ID = "jsoup-xml-formatter";

	/** * The {@link JsoupXmlFormatterWrapper} implementation. */
	private final JsoupXmlFormatterWrapper formatter = new JsoupXmlFormatterWrapper();

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(JsoupXmlFormatterService.class, "FormatterName");
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
		return new JsoupXmlFormatterOptionsPanel(project);
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
			ret = preferences.getInt(JsoupXmlFormatterSettings.INDENT_SIZE, 1);
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
			ret = preferences.getInt(JsoupXmlFormatterSettings.INDENT_SIZE, 1);
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

		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FormatJob getFormatJob(StyledDocument document) {
		return new JsoupFormatJob(document, formatter);
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
				ret = preferences.getInt(JsoupXmlFormatterSettings.INDENT_SIZE, 1);
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

		return isExpandTabToSpaces(document, Settings.getActivePreferences(document));
	}

	private Boolean isExpandTabToSpaces(Document document, Preferences preferences) {
		if (document == null || preferences == null) {
			return null;
		}

		Boolean ret = null;

		if (isUseFormatterIndentationSettings(preferences)) {
			ret = true;
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
