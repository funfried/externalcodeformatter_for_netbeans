/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies;

import javax.swing.text.Document;

/**
 *
 * @author bahlef
 */
public interface IFormatterStrategyService extends IFormatterStrategy {
	Integer getContinuationIndentSize(Document document);

	String getDisplayName();

	String getId();

	Integer getIndentSize(Document document);

	Integer getRightMargin(Document document);

	Integer getSpacesPerTab(Document document);

	Boolean isExpandTabToSpaces(Document document);
}
