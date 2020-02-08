/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui.editor;

/**
 * Copied from private NetBeans classes FmtOptions and SimpleValueNames.
 *
 * @author bahlef
 */
public interface EditorConstants {
	/**
	 * Whether expand typed tabs to spaces. The number of spaces to substitute
	 * per one typed tab is determined by SPACES_PER_TAB setting.
	 * Values: java.lang.Boolean instances
	 */
	static final String EXPAND_TABS = "expand-tabs";

	/**
	 * Number of spaces to draw when the '\t' character
	 * is found in the text. Better said when the drawing-engine
	 * finds a '\t' character it computes the next multiple
	 * of TAB_SIZE and continues drawing from that position.
	 * Values: java.lang.Integer instances
	 */
	static final String TAB_SIZE = "tab-size";

	/**
	 * How many spaces substitute per one typed tab. This parameter has
	 * effect only when EXPAND_TABS setting is set to true.
	 * This parameter has no influence on how
	 * the existing tabs are displayed.
	 * Values: java.lang.Integer instances
	 */
	static final String SPACES_PER_TAB = "spaces-per-tab";

	/**
	 * Shift-width says how many spaces should the formatter use
	 * to indent the more inner level of code. This setting is independent of TAB_SIZE
	 * and SPACES_PER_TAB.
	 * Values: java.lang.Integer instances
	 */
	static final String INDENT_SHIFT_WIDTH = "indent-shift-width";

	/**
	 * Continuation ident size says how many spaces should the formatter use
	 * to indent after a new line of the more inner level of code. This setting
	 * is independent of TAB_SIZE and SPACES_PER_TAB.
	 * Values: java.lang.Integer instances
	 */
	static final String CONTINUATION_INDENT_SIZE = "continuationIndentSize";

	/**
	 * After how many characters the text limit line should be displayed.
	 * Values: java.awt.Integer instances
	 */
	static final String TEXT_LIMIT_WIDTH = "text-limit-width";
}
