/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter;

import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.openide.util.NbBundle;

/**
 * Enum describing the supported mime types for external formatters.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"JAVA=Java",
		"JAVASCRIPT=JavaScript",
		"JSON=Json",
		"XML=XML"
})
public enum MimeType {
	JAVA(JavaTokenId.language().mimeType()), JAVASCRIPT("text/javascript"), JSON("text/x-json", "^text/(.*)\\+x-json$"), XML("text/xml", "^text/(.*)\\+xml$");

	private final String[] mimeTypes;

	private MimeType(String... mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public String getDisplayName() {
		return NbBundle.getMessage(MimeType.class, this.toString());
	}

	public String[] getMimeTypes() {
		return mimeTypes;
	}

	public boolean canHandle(String mimeType) {
		if (StringUtils.isBlank(mimeType)) {
			return false;
		}

		String[] types = getMimeTypes();
		for (String type : types) {
			if (type.startsWith("^") && Pattern.matches(type, mimeType)) {
				return true;
			} else if (Objects.equals(type, mimeType)) {
				return true;
			}
		}

		return false;
	}

	public static MimeType getByMimeType(String mimeType) {
		for (MimeType mime : MimeType.values()) {
			if (mime.canHandle(mimeType)) {
				return mime;
			}
		}

		return null;
	}
}
