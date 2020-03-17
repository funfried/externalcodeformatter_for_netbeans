/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.json.jackson;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Wrapper class to the revelc.net formatter implementation.
 *
 * @author bahlef
 */
public final class JacksonJsonFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link JacksonJsonFormatterWrapper}.
	 */
	JacksonJsonFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code     the unformatted code
	 * @param lineFeed the line feed to use for formatting
	 * @param options  the {@link Options}
	 *
	 * @return the formatted code
	 */
	@CheckForNull
	public String format(String code, String lineFeed, Options options) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		if (lineFeed == null) {
			lineFeed = System.getProperty("line.separator");
		}

		if (options == null) {
			options = new Options();
		}

		int indentSize = options.getIndentSize();
		int spacesPerTab = options.getSpacesPerTab();
		boolean spaceBeforeSeparator = options.isSpaceBeforeSeparator();
		boolean expandTabsToSpaces = options.isExpandTabsToSpaces();

		String indentString;
		if (expandTabsToSpaces) {
			indentString = StringUtils.repeat(" ", indentSize);
		} else {
			indentString = StringUtils.repeat("\t", indentSize / spacesPerTab) + StringUtils.repeat(" ", indentSize % spacesPerTab);
		}

		DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter(indentString, lineFeed);

		DefaultPrettyPrinter printer = new JacksonPrettyPrinter(spaceBeforeSeparator);
		printer.withSeparators(DefaultPrettyPrinter.DEFAULT_SEPARATORS);
		printer.indentObjectsWith(indenter);
		printer.indentArraysWith(indenter);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setDefaultPrettyPrinter(printer);

		JsonFactory factory = objectMapper.getFactory();
		factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
		factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
		factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		factory.enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);

		return format(objectMapper, code);
	}

	/**
	 * Formats the given {@code code} with the given {@link ObjectMapper} and returns
	 * the formatted code.
	 *
	 * @param objectMapper the {@link ObjectMapper}
	 * @param code         the unformatted code
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the given code could not be formatted
	 *                                   with this formatter
	 */
	@CheckForNull
	private String format(ObjectMapper objectMapper, @NonNull String code) throws FormattingFailedException {
		String formattedCode;
		try {
			Object json = objectMapper.readValue(code, Object.class);
			formattedCode = objectMapper.writer().writeValueAsString(json);
			if (Objects.equals(code, formattedCode)) {
				return null;
			}
		} catch (JsonProcessingException ex) {
			throw new FormattingFailedException(ex);
		}

		return formattedCode;
	}

	/**
	 * Custom implementation of the {@link DefaultPrettyPrinter}.
	 */
	private class JacksonPrettyPrinter extends DefaultPrettyPrinter {
		private static final long serialVersionUID = 1L;

		private final boolean spaceBeforeSeparator;

		/**
		 * Creates a new instance of {@link JacksonPrettyPrinter}.
		 *
		 * @param spaceBeforeSeparator {@code true} to add a space between the
		 *                             key and before the value separator
		 */
		public JacksonPrettyPrinter(boolean spaceBeforeSeparator) {
			super();
			this.spaceBeforeSeparator = spaceBeforeSeparator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DefaultPrettyPrinter createInstance() {
			return new DefaultPrettyPrinter(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DefaultPrettyPrinter withSeparators(Separators separators) {
			this._separators = separators;
			this._objectFieldValueSeparatorWithSpaces = (this.spaceBeforeSeparator ? " " : "") + separators.getObjectFieldValueSeparator() + " ";

			return this;
		}
	}

	public static class Options {
		private int indentSize = 2;

		private int spacesPerTab = 2;

		private boolean spaceBeforeSeparator = false;

		private boolean expandTabsToSpaces = true;

		public int getIndentSize() {
			return indentSize;
		}

		public void setIndentSize(int indentSize) {
			this.indentSize = indentSize;
		}

		public int getSpacesPerTab() {
			return spacesPerTab;
		}

		public void setSpacesPerTab(int spacesPerTab) {
			this.spacesPerTab = spacesPerTab;
		}

		public boolean isSpaceBeforeSeparator() {
			return spaceBeforeSeparator;
		}

		public void setSpaceBeforeSeparator(boolean spaceBeforeSeparator) {
			this.spaceBeforeSeparator = spaceBeforeSeparator;
		}

		public boolean isExpandTabsToSpaces() {
			return expandTabsToSpaces;
		}

		public void setExpandTabsToSpaces(boolean expandTabsToSpaces) {
			this.expandTabsToSpaces = expandTabsToSpaces;
		}
	}
}
