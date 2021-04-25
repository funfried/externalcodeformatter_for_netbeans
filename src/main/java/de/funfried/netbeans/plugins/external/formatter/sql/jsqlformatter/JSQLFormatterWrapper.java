/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

import com.manticore.jsqlformatter.JSQLFormatter;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;
import org.netbeans.api.annotations.common.CheckForNull;

/**
 * Delegation class to the Google formatter implementation.
 *
 * @author bahlef
 */
public final class JSQLFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link GoogleJavaFormatterWrapper}.
	 */
	JSQLFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code            the unformatted code
	 * @param options      
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, String... optionsStr) throws FormattingFailedException {
		if (code == null) {
			return null;
		}
		try {
			return JSQLFormatter.format(code, optionsStr);
		} catch (Exception ex) {
			throw new FormattingFailedException(ex);
		}
	}
}
