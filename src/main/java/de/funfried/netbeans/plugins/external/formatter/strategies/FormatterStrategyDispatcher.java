/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * Saad Mufti <saad.mufti@teamaol.com>
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.external.formatter.strategies.netbeans.NetBeansFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Delegation class for calling the activated external formatter or the internal
 * NetBeans formatter if the external formatters are deactivated.
 *
 * @author markiewb
 * @author bahlef
 */
public class FormatterStrategyDispatcher {
	private static final Logger log = Logger.getLogger(FormatterStrategyDispatcher.class.getName());

	private final NetBeansFormatterStrategy netbeansStrategy = new NetBeansFormatterStrategy();

	private static final ReentrantLock lock = new ReentrantLock();

	private static FormatterStrategyDispatcher instance = null;

	private FormatterStrategyDispatcher() {
	}

	/**
	 * Returns the singleton instance of {@link FormatterStrategyDispatcher}.
	 *
	 * @return the singleton instance
	 */
	public static FormatterStrategyDispatcher getInstance() {
		lock.lock();

		try {
			if (instance == null) {
				instance = new FormatterStrategyDispatcher();
			}
		} finally {
			lock.unlock();
		}

		return instance;
	}

	/**
	 * Formats the {@link StyledDocument} given in the {@link FormatterAdvice} in regard to
	 * the values included in that {@link FormatterAdvice}.
	 *
	 * @param fa the {@link FormatterAdvice} containing detailed instruction on what to format
	 */
	public void format(FormatterAdvice fa) {
		try {
			final StyledDocument styledDoc = fa.getStyledDocument();

			IFormatterStrategyService formatterStrategyService = getActiveFormatterStrategyService(styledDoc);
			if (formatterStrategyService != null && formatterStrategyService.canHandle(styledDoc)) {
				try {
					formatterStrategyService.format(fa);
				} catch (FileTypeNotSupportedException ex) {
					log.log(Level.FINE, "Could not use " + formatterStrategyService.getDisplayName() + " for given document", ex);

					// fallback to NetBeans formatter, but should never happen because of canHandle call before
					netbeansStrategy.format(fa);
				}
			} else {
				netbeansStrategy.format(fa);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}
	}

	/**
	 * Returns the continuation indent size configured by the formatter which is
	 * activated for the given {@link Document}, or {@code null} if the internal
	 * NetBeans code formatter is used for the given {@code document}.
	 * 
	 * @param document the {@link Document} for which the continuation indent size
	 *                 is requested
	 *
	 * @return the continuation indent size configured by the formatter which is
	 *         activated for the given {@link Document}, or {@code null} if the
	 *         internal NetBeans code formatter is used for the given {@code document}
	 */
	public Integer getContinuationIndentSize(Document document) {
		try {
			IFormatterStrategyService formatterStrategy = getActiveFormatterStrategyService(document);
			if (formatterStrategy != null && formatterStrategy.canHandle(document)) {
				return formatterStrategy.getContinuationIndentSize(document);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return null;
	}

	/**
	 * Returns the indent size configured by the formatter which is activated for
	 * the given {@link Document}, or {@code null} if the internal NetBeans code
	 * formatter is used for the given {@code document}.
	 * 
	 * @param document the {@link Document} for which the indent size is requested
	 *
	 * @return the indent size configured by the formatter which is activated for
	 *         the given {@link Document}, or {@code null} if the internal NetBeans
	 *         code formatter is used for the given {@code document}
	 */
	public Integer getIndentSize(Document document) {
		try {
			IFormatterStrategyService formatterStrategy = getActiveFormatterStrategyService(document);
			if (formatterStrategy != null && formatterStrategy.canHandle(document)) {
				return formatterStrategy.getIndentSize(document);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return null;
	}

	/**
	 * Returns the right margin (position of the red line in the editor) configured
	 * by the formatter which is activated for the given {@link Document}, or
	 * {@code null} if the internal NetBeans code formatter is used for the given
	 * {@code document}.
	 * 
	 * @param document the {@link Document} for which the right margin is requested
	 *
	 * @return the right margin (position of the red line in the editor) configured
	 *         by the formatter which is activated for the given {@link Document},
	 *         or {@code null} if the internal NetBeans code formatter is used for
	 *         the given {@code document}
	 */
	public Integer getRightMargin(Document document) {
		try {
			IFormatterStrategyService formatterStrategy = getActiveFormatterStrategyService(document);
			if (formatterStrategy != null && formatterStrategy.canHandle(document)) {
				return formatterStrategy.getRightMargin(document);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return null;
	}

	/**
	 * Returns the spaces per tab configured by the formatter which is activated
	 * for the given {@link Document}, or {@code null} if the internal NetBeans
	 * code formatter is used for the given {@code document}.
	 * 
	 * @param document the {@link Document} for which the spaces per tab is requested
	 *
	 * @return the spaces per tab configured by the formatter which is activated
	 *         for the given {@link Document}, or {@code null} if the internal
	 *         NetBeans code formatter is used for the given {@code document}
	 */
	public Integer getSpacesPerTab(Document document) {
		try {
			IFormatterStrategyService formatterStrategy = getActiveFormatterStrategyService(document);
			if (formatterStrategy != null && formatterStrategy.canHandle(document)) {
				return formatterStrategy.getSpacesPerTab(document);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return null;
	}

	private IFormatterStrategyService getActiveFormatterStrategyService(Document document) {
		Preferences prefs = Settings.getActivePreferences(document);
		String activeFormatterStrategyId = prefs.get(Settings.ENABLED_FORMATTER, NetBeansFormatterStrategy.ID);

		Collection<? extends IFormatterStrategyService> formatterStrategyServices = Lookup.getDefault().lookupAll(IFormatterStrategyService.class);
		for (IFormatterStrategyService formatterStrategyService : formatterStrategyServices) {
			if (Objects.equals(activeFormatterStrategyId, formatterStrategyService.getId())) {
				return formatterStrategyService;
			}
		}

		return null;
	}

	/**
	 * Returns the expand tab to spaces flag configured by the formatter which is
	 * activated for the given {@link Document}, or {@code null} if the internal
	 * NetBeans code formatter is used for the given {@code document}.
	 * 
	 * @param document the {@link Document} for which the expand tab to spaces flag is requested
	 *
	 * @return the expand tab to spaces flag configured by the formatter which is
	 *         activated for the given {@link Document}, or {@code null} if the
	 *         internal NetBeans code formatter is used for the given
	 *         {@code document}
	 */
	public Boolean isExpandTabToSpaces(Document document) {
		try {
			IFormatterStrategyService formatterStrategy = getActiveFormatterStrategyService(document);
			if (formatterStrategy != null && formatterStrategy.canHandle(document)) {
				return formatterStrategy.isExpandTabToSpaces(document);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return null;
	}
}
