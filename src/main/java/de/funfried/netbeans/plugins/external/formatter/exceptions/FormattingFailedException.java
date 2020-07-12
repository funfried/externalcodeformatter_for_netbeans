/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.exceptions;

/**
 * {@link RuntimeException} which is thrown when an external formatter
 * failed to format a given code.
 *
 * @author bahlef
 */
public class FormattingFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of {@link FormattingFailedException}.
	 *
	 * @param message the detail message
	 */
	public FormattingFailedException(String message) {
		super(message);
	}

	/**
	 * Creates a new instance of {@link FormattingFailedException}.
	 *
	 * @param cause the original cause of the exception
	 */
	public FormattingFailedException(Throwable cause) {
		super(cause);
	}
}
