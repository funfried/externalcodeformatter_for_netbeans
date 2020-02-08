/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * bahlef
 */
package de.funfried.netbeans.plugins.external.formatter.exceptions;

/**
 * {@link RuntimeException} which is thrown when a configuration file could not be
 * parsed or even loaded.
 *
 * @author bahlef
 */
public class CannotLoadConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of {@link CannotLoadConfigurationException}.
	 *
	 * @param message the detail message
	 */
	public CannotLoadConfigurationException(String message) {
		super(message);
	}

	/**
	 * Creates a new instance of {@link CannotLoadConfigurationException}.
	 *
	 * @param cause the original cause of the exceptio
	 */
	public CannotLoadConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new instance of {@link CannotLoadConfigurationException}.
	 *
	 * @param message the detail message
	 * @param cause   the original cause of the exception
	 */
	public CannotLoadConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
