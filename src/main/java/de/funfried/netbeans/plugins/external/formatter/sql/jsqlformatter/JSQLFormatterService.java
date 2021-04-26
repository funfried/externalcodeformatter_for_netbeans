 /*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

import de.funfried.netbeans.plugins.external.formatter.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import de.funfried.netbeans.plugins.external.formatter.java.base.AbstractJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter.ui.JSQLFormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import java.util.SortedSet;
import java.util.prefs.Preferences;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.project.Project;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Google implementation of the {@link AbstractJavaFormatterService}.
 *
 * @author bahlef
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

		// Cannot handle guarded blocks properly due to a bug in the Google Java Code Formatter:
		// https://github.com/google/google-java-format/issues/433
		if (document instanceof StyledDocument) {
			StyledDocument styledDoc = (StyledDocument) document;

			if (GuardedSectionManager.getInstance(styledDoc) != null) {
				return false;
			}
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

		Integer ret = 4;

		//		Preferences preferences = Settings.getActivePreferences(document);
		//		if (isUseFormatterIndentationSettings(preferences)) {
		//			String codeStylePref = preferences.get(JSQLFormatterSettings.SQL_FORMATTER_CODE_STYLE, "DEFAULT");
		//			JavaFormatterOptions.Style codeStyle = JavaFormatterOptions.Style.valueOf(codeStylePref);
		//			if (JavaFormatterOptions.Style.GOOGLE.equals(codeStyle)) {
		//				// see: https://google.github.io/styleguide/javaguide.html#s4.5.2-line-wrapping-indent
		//				ret = 4;
		//			} else {
		//				// see: https://source.android.com/setup/contribute/code-style#use-spaces-for-indentation
		//				ret = 8;
		//			}
		//		}

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

		Integer ret = 4;

		//		Preferences preferences = Settings.getActivePreferences(document);
		//		if (isUseFormatterIndentationSettings(preferences)) {
		//			String codeStylePref = preferences.get(JSQLFormatterSettings.SQL_FORMATTER_CODE_STYLE, JavaFormatterOptions.Style.GOOGLE.name());
		//			JavaFormatterOptions.Style codeStyle = JavaFormatterOptions.Style.valueOf(codeStylePref);
		//			if (JavaFormatterOptions.Style.GOOGLE.equals(codeStyle)) {
		//				// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
		//				ret = 2;
		//			} else {
		//				// see: https://source.android.com/setup/contribute/code-style#use-spaces-for-indentation
		//				ret = 4;
		//			}
		//		}

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

		// see: https://google.github.io/styleguide/javaguide.html#s4.4-column-limit
		// and https://source.android.com/setup/contribute/code-style#limit-line-length
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

		Integer ret = 4;

		//		Preferences preferences = Settings.getActivePreferences(document);
		//		if (isUseFormatterIndentationSettings(preferences)) {
		//			if (preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, true)) {
		//				ret = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);
		//			} else {
		//				String codeStylePref = preferences.get(JSQLFormatterSettings.SQL_FORMATTER_CODE_STYLE, JavaFormatterOptions.Style.GOOGLE.name());
		//				JavaFormatterOptions.Style codeStyle = JavaFormatterOptions.Style.valueOf(codeStylePref);
		//				if (JavaFormatterOptions.Style.GOOGLE.equals(codeStyle)) {
		//					// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
		//					ret = 2;
		//				} else {
		//					// see: https://source.android.com/setup/contribute/code-style#use-spaces-for-indentation
		//					ret = 4;
		//				}
		//			}
		//		}

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

		Boolean ret = Boolean.TRUE;

		//		Preferences preferences = Settings.getActivePreferences(document);
		//		if (isUseFormatterIndentationSettings(preferences)) {
		//			// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
		//			// and https://source.android.com/setup/contribute/code-style#use-spaces-for-indentation
		//			ret = false;
		//		}

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
