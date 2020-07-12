/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.exceptions;

/**
 * {@link RuntimeException} which is thrown when a given profile name could not be
 * found in a formatter configuration.
 *
 * @author bahlef
 */
public class ProfileNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of {@link ProfileNotFoundException}.
	 *
	 * @param message the detail message
	 */
	public ProfileNotFoundException(String message) {
		super(message);
	}
}
