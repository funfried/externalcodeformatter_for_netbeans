/*
 * Copyright (c) 2013 markiewb.
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

import org.netbeans.modules.editor.NbEditorUtilities;

public interface Utils {
	public static boolean isJava(Document document) {
		return "text/x-java".equals(NbEditorUtilities.getMimeType(document));
	}
}
