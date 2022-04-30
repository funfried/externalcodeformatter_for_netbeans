/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.sqlformatter;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
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
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.sql.sqlformatter.ui.SQLFormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Vertical Blank SQL formatter implementation of the {@link FormatterService}.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Vertical Blank SQL Formatter"
})
@ServiceProvider(service = FormatterService.class, position = 500)
public class SQLFormatterService implements FormatterService {
	/** The ID of this formatter service. */
	public static final String ID = "vertical-blank-sql-formatter";

	/** * The {@link SQLFormatterWrapper} implementation. */
	private final SQLFormatterWrapper formatter = new SQLFormatterWrapper();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) throws BadLocationException, FormattingFailedException {
		if (!canHandle(document)) {
			throw new FormattingFailedException("The file type '" + MimeType.getMimeTypeAsString(document) + "' is not supported");
		}

		getFormatJob(document, changedElements).format();

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MimeType> getSupportedMimeTypes() {
		return Collections.singletonList(MimeType.SQL);
	}

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(SQLFormatterService.class, "FormatterName");
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
		return new SQLFormatterOptionsPanel(project);
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

		Integer width = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			width = preferences.getInt(SQLFormatterSettings.INDENT_SIZE, SQLFormatterSettings.INDENT_SIZE_DEFAULT);
		}

		return width;
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

		Integer width = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			width = preferences.getInt(SQLFormatterSettings.INDENT_SIZE, SQLFormatterSettings.INDENT_SIZE_DEFAULT);
		}

		return width;
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

		Preferences preferences = Settings.getActivePreferences(document);
		return preferences.getInt(SQLFormatterSettings.MAX_COLUMN_LENGTH, SQLFormatterSettings.MAX_COLUMN_LENGTH_DEFAULT);
	}

	/**
	 * {@inheritDoc}
	 */
	protected FormatJob getFormatJob(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
		return new SQLFormatterJob(document, formatter, changedElements);
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

		Integer width = null;

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			width = preferences.getInt(SQLFormatterSettings.INDENT_SIZE, SQLFormatterSettings.INDENT_SIZE_DEFAULT);
		}

		return width;
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

		Preferences preferences = Settings.getActivePreferences(document);
		if (isUseFormatterIndentationSettings(preferences)) {
			return preferences.getBoolean(SQLFormatterSettings.EXPAND_TABS_TO_SPACES, SQLFormatterSettings.EXPAND_TABS_TO_SPACES_DEFAULT);
		}

		return null;
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
