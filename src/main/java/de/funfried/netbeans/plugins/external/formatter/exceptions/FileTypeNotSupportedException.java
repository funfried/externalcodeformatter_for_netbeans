/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.exceptions;

/**
 *
 * @author bahlef
 */
public class FileTypeNotSupportedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FileTypeNotSupportedException(String message) {
		super(message);
	}

	public FileTypeNotSupportedException(Throwable cause) {
		super(cause);
	}

	public FileTypeNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}
}
