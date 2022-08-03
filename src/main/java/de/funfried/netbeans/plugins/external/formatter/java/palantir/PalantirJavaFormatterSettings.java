/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.palantir;

/**
 * Utility class for Palantir Java formatter specific settings.
 *
 * @author bahlef
 */
public class PalantirJavaFormatterSettings {

	/**
	 * Property key which defines if the Palantir formatter internal organize imports action should be used.
	 *
	 * @since 1.15.2
	 */
	public static final String ORGANIZE_IMPORTS = "palantirFormatterOrganizeImports";

	/**
	 * Property key which defines if the Palantir formatter internal organize imports action should be used after NetBeans fix import action was executed.
	 *
	 * @since 1.15.2
	 */
	public static final String ORGANIZE_IMPORTS_AFTER_FIX_IMPORTS = "palantirFormatterOrganizeImportsAfterFixImports";

	/**
	 * Private contructor because of static methods only.
	 */
	private PalantirJavaFormatterSettings() {
	}
}
