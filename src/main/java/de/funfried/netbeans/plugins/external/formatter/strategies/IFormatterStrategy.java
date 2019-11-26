/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import javax.swing.text.StyledDocument;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public interface IFormatterStrategy {
	boolean canHandle(StyledDocument document);

	void format(ParameterObject po);
}
