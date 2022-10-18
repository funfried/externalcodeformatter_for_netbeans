/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.css.cssparser;

import java.io.IOException;
import java.io.StringReader;

import org.netbeans.api.annotations.common.CheckForNull;
import org.w3c.css.sac.InputSource;

import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Delegation class to the CssParser formatter implementation.
 *
 * @author bahlef
 */
public final class CssParserFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link CssParserFormatterWrapper}.
	 */
	CssParserFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted code
	 * @param indent the indent to use
	 * @param rgbAsHex {@code true} to prefer hex over rgb, otherwise {@code false}
	 * @param useSingleQuotes {@code true} to prefer single quotes over double quotes, otherwise {@code false}
	 * @param useSourceStringValues {@code true} to use source string values, otherwise {@code false}
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, int indent, boolean rgbAsHex, boolean useSingleQuotes, boolean useSourceStringValues) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		CSSFormat formatter = new CSSFormat()
				.setPropertiesInSeparateLines(indent)
				.setRgbAsHex(rgbAsHex)
				.setUseSingleQuotes(useSingleQuotes)
				.setUseSourceStringValues(useSourceStringValues);

		try (StringReader reader = new StringReader(code)) {
			InputSource source = new InputSource(reader);
			CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
			CSSStyleSheetImpl sheet = (CSSStyleSheetImpl) parser.parseStyleSheet(source, null, null);

			String formattedCode = sheet.getCssText(formatter);

			// Patch converted 'tab' back to '\9' for IE 7,8, and 9 hack. Cssparser switches it to 'tab'.
			return formattedCode.replace("\t;", "\\9;");
		} catch (IOException ex) {
			throw new FormattingFailedException(ex);
		}
	}
}
