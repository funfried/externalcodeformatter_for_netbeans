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

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.NbBundle;

/**
 * Enum describing the supported mime types for external formatters.
 *
 * @author bahlef
 */
@NbBundle.Messages({
		"CSS=CSS",
		"HTML=HTML",
		"JAVA=Java",
		"JAVASCRIPT=JavaScript",
		"JSON=Json",
		"XML=XML",
		"SQL=SQL"
})
public enum MimeType {
	//	CSS("text/css"), HTML("application/xhtml+xml", "text/html"),
	JAVA(JavaTokenId.language().mimeType()), JAVASCRIPT("text/javascript"), JSON("text/x-json", "^text/(.*)\\+x-json$"), XML("text/xml",
			"^text/(.*)\\+xml$"), SQL("application/sql", "text/sql", "text/x-sql", "text/plain");

	private final String[] mimeTypes;

	MimeType(String... mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public String getDisplayName() {
		return NbBundle.getMessage(MimeType.class, this.toString());
	}

	public boolean canHandle(String mimeType) {
		if (StringUtils.isBlank(mimeType)) {
			return false;
		}

		for (String type : mimeTypes) {
			if (type.startsWith("^") && Pattern.matches(type, mimeType)) {
				return true;
			} else if (Objects.equals(type, mimeType)) {
				return true;
			}
		}

		return false;
	}

	public static boolean canHandle(List<MimeType> types, String mimeType) {
		if (StringUtils.isBlank(mimeType)) {
			return false;
		}

		for (MimeType t : types) {
			for (String type : t.mimeTypes) {
				if (type.startsWith("^") && Pattern.matches(type, mimeType)) {
					return true;
				} else if (Objects.equals(type, mimeType)) {
					return true;
				}
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

	public static MimeType getMimeType(Document document) {
		return getByMimeType(getMimeTypeAsString(document));
	}

	public static String getMimeTypeAsString(Document document) {
		if (document != null) {
			return NbEditorUtilities.getMimeType(document);
		}

		return null;
	}
}
