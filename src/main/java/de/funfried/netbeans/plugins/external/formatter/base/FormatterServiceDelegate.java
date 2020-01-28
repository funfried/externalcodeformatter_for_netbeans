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
package de.funfried.netbeans.plugins.external.formatter.base;

import de.funfried.netbeans.plugins.external.formatter.base.FormatterService;

import java.util.Collection;
import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Singleton delegation class for calling the activated external formatter, if no external formatter is
 * activated nothing will be done by this implementation. There is no fallback to the internal NetBeans
 * formatter in here!
 *
 * @author markiewb
 * @author bahlef
 */
public class FormatterServiceDelegate {
	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(FormatterServiceDelegate.class.getName());

	/** {@link ReentrantLock} to synchronize the singleton instance creation. */
	private static final ReentrantLock lock = new ReentrantLock();

	/** *  Singleton instance of {@link FormatterServiceDelegate}. */
	private static FormatterServiceDelegate instance = null;

	/**
	 * Private contructor due to singleton pattern.
	 */
	private FormatterServiceDelegate() {
	}

	/**
	 * Returns the singleton instance of {@link FormatterServiceDelegate}.
	 *
	 * @return the singleton instance
	 */
	@NonNull
	public static FormatterServiceDelegate getInstance() {
		lock.lock();

		try {
			if (instance == null) {
				instance = new FormatterServiceDelegate();
			}
		} finally {
			lock.unlock();
		}

		return instance;
	}

	/**
	 * Formats the given {@link StyledDocument} in regard to the given {@code changedElements}.
	 *
	 * @param document        the {@link StyledDocument} which should be formatted
	 * @param changedElements a {@link SortedSet} containing ranges as {@link Pair} objects that should be formatted
	 *
	 * @return {@code true} if and only if a external formatter was found to handle the given
	 *         {@link StyledDocument} even if there was an error while formatting, otherwise
	 *         {@code false}
	 */
	public boolean format(StyledDocument document, SortedSet<Pair<Integer, Integer>> changedElements) {
		try {
			FormatterService formatterService = getActiveFormatterService(document);
			if (formatterService != null && formatterService.canHandle(document)) {
				try {
					formatterService.format(document, changedElements);
				} catch (FileTypeNotSupportedException ex) {
					// Should never be thrown, should already be checked by canHandle call
					log.log(Level.WARNING, "Could not use " + formatterService.getDisplayName() + " for given document", ex);
				} catch (BadLocationException ex) {
					log.log(Level.SEVERE, formatterService.getDisplayName() + " failed to format the code", ex);
				}

				return true;
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return false;
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
	@CheckForNull
	public Integer getContinuationIndentSize(Document document) {
		try {
			FormatterService formatterService = getActiveFormatterService(document);
			if (formatterService != null && formatterService.canHandle(document)) {
				return formatterService.getContinuationIndentSize(document);
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
	@CheckForNull
	public Integer getIndentSize(Document document) {
		try {
			FormatterService formatterService = getActiveFormatterService(document);
			if (formatterService != null && formatterService.canHandle(document)) {
				return formatterService.getIndentSize(document);
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
	@CheckForNull
	public Integer getRightMargin(Document document) {
		try {
			FormatterService formatterService = getActiveFormatterService(document);
			if (formatterService != null && formatterService.canHandle(document)) {
				return formatterService.getRightMargin(document);
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
	@CheckForNull
	public Integer getSpacesPerTab(Document document) {
		try {
			FormatterService formatterService = getActiveFormatterService(document);
			if (formatterService != null && formatterService.canHandle(document)) {
				return formatterService.getSpacesPerTab(document);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return null;
	}

	/**
	 * Returns the currently configured {@link FormatterService} implementation for
	 * the given {@code document}. If the internal NetBeans formatter is configured
	 * or the implementation of the configured {@link FormatterService} could not be
	 * found {@code null} will be returned.
	 *
	 * @param document the {@link Document} for which the {@link FormatterService} is requested
	 *
	 * @return the currently configured {@link FormatterService} implementation
	 *         for the given {@code document}. If the internal NetBeans formatter is configured
	 *         or the implementation of the configured {@link FormatterService} could
	 *         not be found {@code null} will be returned
	 */
	@CheckForNull
	private FormatterService getActiveFormatterService(Document document) {
		Preferences prefs = Settings.getActivePreferences(document);
		String activeFormatterId = prefs.get(Settings.ENABLED_FORMATTER, Settings.DEFAULT_FORMATTER);

		Collection<? extends FormatterService> formatterServices = Lookup.getDefault().lookupAll(FormatterService.class);
		for (FormatterService formatterService : formatterServices) {
			if (Objects.equals(activeFormatterId, formatterService.getId())) {
				return formatterService;
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
	@CheckForNull
	public Boolean isExpandTabToSpaces(Document document) {
		try {
			FormatterService formatterService = getActiveFormatterService(document);
			if (formatterService != null && formatterService.canHandle(document)) {
				return formatterService.isExpandTabToSpaces(document);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}

		return null;
	}
}
