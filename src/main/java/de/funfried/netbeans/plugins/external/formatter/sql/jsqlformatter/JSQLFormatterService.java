 /*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

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

import com.manticore.jsqlformatter.JSQLFormatter;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.java.base.AbstractJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter.ui.JSQLFormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * JSQLFormatter implementation of the {@link AbstractJavaFormatterService}.
 *
 * @author Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 */

@NbBundle.Messages({
		"FormatterName=JSQLFormatter"
})

@ServiceProvider(service = FormatterService.class, position = 500)
public class JSQLFormatterService implements FormatterService {
	/** The ID of this formatter service. */
	public static final String ID = "jsqlformatter";

	/** * The {@link JSQLFormatterWrapper} implementation. */
	private final JSQLFormatterWrapper formatter = new JSQLFormatterWrapper();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) throws BadLocationException, FormattingFailedException {
		if (!canHandle(document)) {
			throw new FormattingFailedException("The file type '" + MimeType.getMimeTypeAsString(document) + "' is not supported");
		}

		getFormatJob(document, changedElements).format();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MimeType getSupportedMimeType() {
		return MimeType.SQL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(Document document) {
		if (document == null) {
			return false;
		}

		return getSupportedMimeType().canHandle(MimeType.getMimeTypeAsString(document));
	}

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(JSQLFormatterService.class, "FormatterName");
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
		return new JSQLFormatterOptionsPanel(project);
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

		Preferences prefs = Settings.getActivePreferences(document);
		int width = prefs.getInt(JSQLFormatter.FormattingOption.INDENT_WIDTH.toString(), JSQLFormatter.getIndentWidth());

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

		Preferences prefs = Settings.getActivePreferences(document);
		int width = prefs.getInt(JSQLFormatter.FormattingOption.INDENT_WIDTH.toString(), JSQLFormatter.getIndentWidth());

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
		return 120;
	}

	/**
	 * {@inheritDoc}
	 */
	protected FormatJob getFormatJob(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
		return new JSQLFormatterJob(document, formatter, changedElements);
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

		Preferences prefs = Settings.getActivePreferences(document);
		int width = prefs.getInt(JSQLFormatter.FormattingOption.INDENT_WIDTH.toString(), JSQLFormatter.getIndentWidth());

		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	@Override
	public Boolean isExpandTabToSpaces(Document document) {
		return Boolean.TRUE;
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
