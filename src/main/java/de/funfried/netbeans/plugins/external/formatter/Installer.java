/*
 * Copyright (c) 2022 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {
	private static final long serialVersionUID = -4271835537575254490L;

	@Override
	public void validate() throws IllegalStateException {
		super.validate();

		if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
			throw new IllegalStateException("This plugin only works with Java 11+, but NetBeans is running with Java " + SystemUtils.JAVA_VERSION);
		}
	}
}
