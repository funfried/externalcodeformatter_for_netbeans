/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.google;

/**
 * Utility class for Google Java formatter specific settings.
 *
 * @author bahlef
 */
public class GoogleJavaFormatterSettings {
	/**
	 * Property key which defines which code style should be used for the Google formatter.
	 *
	 * @since 1.13
	 */
	public static final String GOOGLE_FORMATTER_CODE_STYLE = "googleFormatterCodeStyle";

	/**
	 * Private contructor because of static methods only.
	 */
	private GoogleJavaFormatterSettings() {
	}
}
