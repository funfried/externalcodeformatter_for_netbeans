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

/**
 * Utility class for Spring Java formatter specific settings.
 *
 * @author bahlef
 */
public class SpringJavaFormatterSettings {

	/**
	 * Property key which defines the line feed setting for the Spring formatter.
	 *
	 * @since 1.14
	 */
	public static final String LINEFEED = "spring-linefeed";

	/**
	 * Private contructor because of static methods only.
	 */
	private SpringJavaFormatterSettings() {
	}
}
