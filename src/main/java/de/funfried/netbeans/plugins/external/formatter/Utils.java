/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * Saad Mufti <saad.mufti@teamaol.com>
 * bahlef
 */
package de.funfried.netbeans.plugins.external.formatter;

import javax.swing.text.Document;

import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.modules.editor.NbEditorUtilities;

/**
 * Utility class.
 *
 * @author markiewb
 * @author bahlef
 */
public interface Utils {
	/**
	 * Checks if the mime type of a given {@link Document} is {@code text/x-java}
	 * and returns {@code true} if it is, otherwise {@code false}.
	 *
	 * @param document the {@link Document} where to check the mime type
	 *
	 * @return {@code true} if the mime type is {@code text/x-java}, otherwise
	 *         {@code false}
	 */
	static boolean isJava(Document document) {
		if (document == null) {
			return false;
		}

		return JavaTokenId.language().mimeType().equals(NbEditorUtilities.getMimeType(document));
	}
}
