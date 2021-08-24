/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.xml.jsoup;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

/**
 * Wrapper class to the Jsoup formatter implementation.
 *
 * @author bahlef
 */
public final class JsoupXmlFormatterWrapper {
	/** Default system line separator. */
	private static final String DEFAULT_LINE_SEPARATOR = "\n";

	/**
	 * Package private Constructor for creating a new instance of {@link JsoupXmlFormatterWrapper}.
	 */
	JsoupXmlFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted code
	 * @param lineFeed the line feed to use for formatting
	 * @param options the {@link Document.OutputSettings}
	 *
	 * @return the formatted code
	 */
	@CheckForNull
	public String format(String code, String lineFeed, Document.OutputSettings options) {
		if (code == null) {
			return null;
		}

		if (lineFeed == null) {
			lineFeed = System.getProperty("line.separator");
		}

		if (options == null) {
			options = new Document.OutputSettings();
		}

		options.escapeMode(Entities.EscapeMode.xhtml);
		options.syntax(Document.OutputSettings.Syntax.xml);

		return format(options, code, lineFeed);
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param options the {@link Document.OutputSettings}
	 * @param code the unformatted code
	 * @param lineFeed the line feed to use for formatting
	 *
	 * @return the formatted code
	 */
	@CheckForNull
	private String format(Document.OutputSettings options, @NonNull String code, String lineFeed) {
		Document document = Jsoup.parse(code, "", Parser.xmlParser());
		document.outputSettings(options);

		String formattedCode = document.outerHtml();
		if (Objects.equals(code, formattedCode)) {
			return null;
		} else if (!DEFAULT_LINE_SEPARATOR.equals(lineFeed)) {
			formattedCode = StringUtils.replace(formattedCode, DEFAULT_LINE_SEPARATOR, lineFeed);
		}

		return formattedCode;
	}
}
