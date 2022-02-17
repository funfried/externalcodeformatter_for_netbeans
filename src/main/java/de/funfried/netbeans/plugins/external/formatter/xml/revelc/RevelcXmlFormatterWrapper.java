/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.xml.revelc;

import java.util.Objects;

import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

import net.revelc.code.formatter.xml.lib.FormattingPreferences;
import net.revelc.code.formatter.xml.lib.XmlDocumentFormatter;

/**
 * Wrapper class to the revelc.net formatter implementation.
 *
 * @author bahlef
 */
public final class RevelcXmlFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link RevelcXmlFormatterWrapper}.
	 */
	RevelcXmlFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted code
	 * @param lineFeed the line feed to use for formatting
	 * @param prefs the {@link FormattingPreferences}
	 *
	 * @return the formatted code
	 */
	@CheckForNull
	public String format(String code, String lineFeed, FormattingPreferences prefs) {
		if (code == null) {
			return null;
		}

		if (lineFeed == null) {
			lineFeed = System.getProperty("line.separator");
		}

		if (prefs == null) {
			prefs = new FormattingPreferences();
		}

		XmlDocumentFormatter xmlFormatter = new XmlDocumentFormatter(lineFeed, prefs);

		return format(xmlFormatter, code);
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param xmlFormatter the {@link XmlDocumentFormatter}
	 * @param code the unformatted code
	 *
	 * @return the formatted code
	 */
	@CheckForNull
	private String format(XmlDocumentFormatter xmlFormatter, @NonNull String code) {
		String formattedCode = xmlFormatter.format(code);
		if (Objects.equals(code, formattedCode)) {
			return null;
		}

		return formattedCode;
	}
}
