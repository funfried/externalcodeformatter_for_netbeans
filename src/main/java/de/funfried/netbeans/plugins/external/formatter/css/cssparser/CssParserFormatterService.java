/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.css.cssparser;

import java.util.prefs.Preferences;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.project.Project;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.css.base.AbstractCssFormatterService;
import de.funfried.netbeans.plugins.external.formatter.css.cssparser.ui.CssParserFormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * CssParser implementation of the {@link AbstractCssFormatterService}.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=CssParser"
})
@ServiceProvider(service = FormatterService.class, position = 500)
public class CssParserFormatterService extends AbstractCssFormatterService {
	/** The ID of this formatter service. */
	public static final String ID = "css-parser";

	/** * The {@link CssParserFormatterWrapper} implementation. */
	private final CssParserFormatterWrapper formatter = new CssParserFormatterWrapper();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(Document document) {
		// Cannot handle guarded blocks properly due to a bug in the Google Java Code Formatter:
		// https://github.com/google/google-java-format/issues/433
		if (document instanceof StyledDocument) {
			StyledDocument styledDoc = (StyledDocument) document;

			if (GuardedSectionManager.getInstance(styledDoc) != null) {
				return false;
			}
		}

		return super.canHandle(document);
	}

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(CssParserFormatterService.class, "FormatterName");
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
		return new CssParserFormatterOptionsPanel(project);
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
			ret = preferences.getInt(CssParserFormatterSettings.INDENT, CssParserFormatterSettings.INDENT_DEFAULT);
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
			ret = preferences.getInt(CssParserFormatterSettings.INDENT, CssParserFormatterSettings.INDENT_DEFAULT);
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
		return new CssParserFormatJob(document, formatter);
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
			ret = preferences.getInt(CssParserFormatterSettings.INDENT, CssParserFormatterSettings.INDENT_DEFAULT);
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
