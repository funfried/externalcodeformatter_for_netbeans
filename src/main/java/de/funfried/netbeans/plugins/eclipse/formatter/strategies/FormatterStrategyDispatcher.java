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
package de.funfried.netbeans.plugins.eclipse.formatter.strategies;

import de.funfried.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatterStrategy;
import de.funfried.netbeans.plugins.eclipse.formatter.strategies.netbeans.NetBeansFormatterStrategy;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.StyledDocument;

import org.openide.util.Exceptions;

import de.funfried.netbeans.plugins.eclipse.formatter.exceptions.FileTypeNotSupportedException;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class FormatterStrategyDispatcher {
	private static final Logger log = Logger.getLogger(FormatterStrategyDispatcher.class.getName());

	private final EclipseFormatterStrategy eclipseStrategy = new EclipseFormatterStrategy();

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

	public void format(ParameterObject po) {
		try {
			final StyledDocument styledDoc = po.styledDoc;

			if (eclipseStrategy.canHandle(styledDoc)) {
				try {
					eclipseStrategy.format(po);
				} catch (FileTypeNotSupportedException ex) {
					log.log(Level.FINE, "Could not use Eclipse formatter for given document", ex);

					// fallback to NetBeans formatter, but should not be possible because of canHandle call before
					netbeansStrategy.format(po);
				}
			} else {
				netbeansStrategy.format(po);
			}
		} catch (Exception e) {
			Exceptions.printStackTrace(e);
		}
	}
}
