/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.spring;

import de.funfried.netbeans.plugins.external.formatter.java.spring.ui.SpringJavaFormatterOptionsPanel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.base.FormatJob;
import de.funfried.netbeans.plugins.external.formatter.base.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.base.java.AbstractJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import io.spring.javaformat.formatter.Formatter;

/**
 * Spring implementation of the {@link AbstractJavaFormatterService}.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Spring Java Code Formatter"
})
@ServiceProvider(service = FormatterService.class, position = 1500)
public class SpringJavaFormatterService extends AbstractJavaFormatterService {
	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(SpringJavaFormatterService.class.getName());

	/** The ID of this formatter service. */
	public static final String ID = "spring-java-formatter";

	/** * The {@link SpringJavaFormatterWrapper} implementation. */
	private final SpringJavaFormatterWrapper formatter = new SpringJavaFormatterWrapper();

	/**
	 * {@inheritDoc}
	 */
	@NonNull
	@Override
	public String getDisplayName() {
		return NbBundle.getMessage(SpringJavaFormatterService.class, "FormatterName");
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
		return new SpringJavaFormatterOptionsPanel();
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
			String propKey = "org.eclipse.jdt.core.formatter.continuation_indentation";
			String prop = this.getSpringFormatterProperty(propKey);
			if (prop != null) {
				try {
					ret = Integer.parseInt(prop);
				} catch (NumberFormatException ex) {
					log.log(Level.WARNING, "Property '" + propKey + "' is not an integer: " + prop, ex);
				}
			}

			if (ret == null) {
				ret = 2;
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
			String propKey = "org.eclipse.jdt.core.formatter.indentation.size";
			String prop = this.getSpringFormatterProperty(propKey);
			if (prop != null) {
				try {
					ret = Integer.parseInt(prop);
				} catch (NumberFormatException ex) {
					log.log(Level.WARNING, "Property '" + propKey + "' is not an integer: " + prop, ex);
				}
			}

			if (ret == null) {
				ret = 4;
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

		Integer ret = 120;

		String propKey = "org.eclipse.jdt.core.formatter.lineSplit";
		String prop = this.getSpringFormatterProperty(propKey);
		if (prop != null) {
			try {
				ret = Integer.parseInt(prop);
			} catch (NumberFormatException ex) {
				log.log(Level.WARNING, "Property '" + propKey + "' is not an integer: " + prop, ex);
			}
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FormatJob getFormatJob(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
		return new SpringFormatJob(document, formatter, changedElements);
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
				String propKey = "org.eclipse.jdt.core.formatter.tabulation.size";
				String prop = this.getSpringFormatterProperty(propKey);
				if (prop != null) {
					try {
						ret = Integer.parseInt(prop);
					} catch (NumberFormatException ex) {
						log.log(Level.WARNING, "Property '" + propKey + "' is not an integer: " + prop, ex);
					}
				}

				if (ret == null) {
					ret = 4;
				}
			}
		}

		return ret;
	}

	/**
	 * Reads the configuration value of the internal Spring formatter configuration for the
	 * given {@code  key}.
	 *
	 * @param key the key of the value which should be read
	 *
	 * @return the configuration value of the internal Spring formatter configuration for
	 *         the given {@code  key}
	 */
	private String getSpringFormatterProperty(String key) {
		Properties props = new Properties();
		try (InputStream is = Formatter.class.getResourceAsStream("formatter.prefs")) {
			props.load(is);
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Could not read internal Spring formatter configuration", ex);
		}

		return props.getProperty(key);
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
			String propKey = "org.eclipse.jdt.core.formatter.tabulation.size";
			String prop = this.getSpringFormatterProperty(propKey);
			if (prop != null) {
				ret = Objects.equals(prop, "space");
			}

			if (ret == null) {
				ret = false;
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
