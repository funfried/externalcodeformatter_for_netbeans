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

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.StyledDocument;

import org.openide.util.Exceptions;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.EclipseFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.strategies.google.GoogleFormatterStrategy;
import de.funfried.netbeans.plugins.external.formatter.strategies.netbeans.NetBeansFormatterStrategy;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class FormatterStrategyDispatcher {
	private static final Logger log = Logger.getLogger(FormatterStrategyDispatcher.class.getName());

	private final EclipseFormatterStrategy eclipseStrategy = new EclipseFormatterStrategy();

	private final GoogleFormatterStrategy googleStrategy = new GoogleFormatterStrategy();

	private final NetBeansFormatterStrategy netbeansStrategy = new NetBeansFormatterStrategy();

	private static final ReentrantLock lock = new ReentrantLock();

	private static FormatterStrategyDispatcher instance = null;

	private FormatterStrategyDispatcher() {
	}

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

	public void format(FormatterAdvice fa) {
		try {
			final StyledDocument styledDoc = fa.getStyledDocument();

			if (eclipseStrategy.canHandle(styledDoc)) {
				try {
					eclipseStrategy.format(fa);
				} catch (FileTypeNotSupportedException ex) {
					log.log(Level.FINE, "Could not use Eclipse formatter for given document", ex);

					// fallback to NetBeans formatter, but should not be possible because of canHandle call before
					netbeansStrategy.format(fa);
				}
			} else if (googleStrategy.canHandle(styledDoc)) {
				try {
					googleStrategy.format(fa);
				} catch (FileTypeNotSupportedException ex) {
					log.log(Level.FINE, "Could not use Google formatter for given document", ex);

					// fallback to NetBeans formatter, but should not be possible because of canHandle call before
					netbeansStrategy.format(fa);
				}
			} else {
				netbeansStrategy.format(fa);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}
	}
}
