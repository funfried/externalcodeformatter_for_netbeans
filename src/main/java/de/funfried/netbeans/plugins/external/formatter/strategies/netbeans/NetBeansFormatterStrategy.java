/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.netbeans;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

import de.funfried.netbeans.plugins.external.formatter.strategies.FormatterAdvice;
import de.funfried.netbeans.plugins.external.formatter.strategies.IFormatterStrategy;

/**
 * NetBeans implementation of the {@link IFormatterStrategy}.
 *
 * @author markiewb
 * @author bahlef
 */
public class NetBeansFormatterStrategy implements IFormatterStrategy {
	public static final String ID = "netbeans-formatter";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(Document document) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void format(FormatterAdvice fa) {
		StyledDocument document = fa.getStyledDocument();

		Reformat rf = Reformat.get(document);
		rf.lock();

		// only care about selection if reformatting on menu action and not on file save
		int _dot = !fa.isForSave() ? fa.getSelectionStart() : -1;
		int _mark = !fa.isForSave() ? fa.getSelectionEnd() : -1;

		try {
			NbDocument.runAtomicAsUser(document, new NetBeansFormatterRunnable(document, rf, _dot, _mark));
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		} finally {
			rf.unlock();
		}
	}
}
