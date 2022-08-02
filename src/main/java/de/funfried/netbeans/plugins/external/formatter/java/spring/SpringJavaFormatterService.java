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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.java.base.AbstractJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.java.spring.ui.SpringJavaFormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.FormatterOptionsPanel;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;
import io.spring.javaformat.formatter.eclipse.EclipseCodeFormatter;

/**
 * Spring implementation of the {@link AbstractJavaFormatterService}.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"FormatterName=Spring Java Code Formatter"
})
@ServiceProvider(service = FormatterService.class, position = 1500)
public class SpringJavaFormatterService extends AbstractJavaFormatterService<SpringFormatJob> {
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
	public FormatterOptionsPanel createOptionsPanel(Project project) {
		return new SpringJavaFormatterOptionsPanel(project);
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
			String propKey = "core.formatter.continuation_indentation";
			String prop = this.getSpringFormatterProperty(propKey);
			if (prop != null) {
				try {
					ret = Integer.parseInt(prop);

					Integer indentSize = getIndentSize(document);
					if (indentSize != null) {
						ret *= indentSize;
					}
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
			String tabChar = getSpringFormatterProperty("core.formatter.tabulation.char");
			if (Objects.equals(tabChar, "mixed")) {
				String propKey = "core.formatter.indentation.size";
				String prop = this.getSpringFormatterProperty(propKey);
				if (prop != null) {
					try {
						ret = Integer.parseInt(prop);
					} catch (NumberFormatException ex) {
						log.log(Level.WARNING, "Property '" + propKey + "' is not an integer: " + prop, ex);
					}
				}
			} else {
				ret = getSpacesPerTab(document);
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

		String propKey = "core.formatter.lineSplit";
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
	protected SpringFormatJob getFormatJob(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
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
			if (!isExpandTabToSpaces(document, preferences) && preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, true)) {
				ret = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);
			} else {
				String propKey = "core.formatter.tabulation.size";
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
		try (InputStream is = EclipseCodeFormatter.class.getResourceAsStream("formatter.prefs")) {
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

		return isExpandTabToSpaces(document, Settings.getActivePreferences(document));
	}

	private Boolean isExpandTabToSpaces(Document document, Preferences preferences) {
		if (document == null || preferences == null) {
			return null;
		}

		Boolean ret = null;

		if (isUseFormatterIndentationSettings(preferences)) {
			String propKey = "core.formatter.tabulation.char";
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

	@Override
	public Boolean organizeImports(StyledDocument document, boolean afterFixImports) throws BadLocationException {
		return null;
	}
}
