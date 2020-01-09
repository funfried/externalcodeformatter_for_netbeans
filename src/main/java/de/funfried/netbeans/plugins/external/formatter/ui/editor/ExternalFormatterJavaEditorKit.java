/*
 * Copyright (c) 2019 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui.editor;

import javax.swing.text.Document;

import org.netbeans.modules.editor.java.JavaKit;

/**
 *
 * @author bahlef
 */
public class ExternalFormatterJavaEditorKit extends JavaKit {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document createDefaultDocument() {
		return new ExternalFormatterJavaDocument();
	}
}
