/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.google;

import java.util.SortedSet;
import java.util.prefs.Preferences;

import javax.annotation.Nullable;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractJavaFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.strategies.IFormatterStrategyService;
import de.funfried.netbeans.plugins.external.formatter.strategies.netbeans.NetBeansFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Google Java Code Formatter"
})
@ServiceProvider(service = IFormatterStrategyService.class)
public class GoogleFormatterStrategy extends AbstractJavaFormatterStrategy {
	public static final String ID = "google-java-formatter";

	private final GoogleFormatter formatter = new GoogleFormatter();

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
	@Override
	protected void format(StyledDocument document, int dot, int mark, SortedSet<Pair<Integer, Integer>> changedElements) {
		GoogleFormatterRunnable formatterRunnable = new GoogleFormatterRunnable(document, formatter, dot, mark, changedElements);
		formatterRunnable.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@NotNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(GoogleFormatterStrategy.class, "FormatterName");
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
			// see: https://google.github.io/styleguide/javaguide.html#s4.5.2-line-wrapping-indent
			ret = 4;
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
			// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
			ret = 2;
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

		// see: https://google.github.io/styleguide/javaguide.html#s4.4-column-limit

		return 100;
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
				// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
				ret = 2;
			}
		}

		return ret;
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
			// see: https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation
			ret = false;
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
