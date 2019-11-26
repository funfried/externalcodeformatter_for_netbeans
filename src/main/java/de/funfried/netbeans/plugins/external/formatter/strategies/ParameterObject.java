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

import java.util.SortedSet;

import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author markiewb
 */
public class ParameterObject {
	public StyledDocument styledDoc;

	public SortedSet<Pair<Integer, Integer>> changedElements;

	public boolean forSave;

	public int selectionStart;

	public int selectionEnd;

	public int caret;

	public JTextComponent editor;
}
