/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * Saad Mufti <saad.mufti@teamaol.com>
 */
package de.funfried.netbeans.plugins.eclipse.formatter.onsave;

import de.funfried.netbeans.plugins.eclipse.formatter.strategies.ParameterObject;
import de.funfried.netbeans.plugins.eclipse.formatter.strategies.FormatterStrategyDispatcher;
import static de.funfried.netbeans.plugins.eclipse.formatter.options.Preferences.ENABLE_SAVEACTION;
import static de.funfried.netbeans.plugins.eclipse.formatter.options.Preferences.ENABLE_SAVEACTION_MODIFIEDLINESONLY;
import static de.funfried.netbeans.plugins.eclipse.formatter.options.Preferences.FEATURE_formatChangedLinesOnly;
import static de.funfried.netbeans.plugins.eclipse.formatter.options.Preferences.getActivePreferences;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

public class FormatOnSaveTask implements OnSaveTask {

	private static final Logger LOG = Logger.getLogger(FormatOnSaveTask.class.getName());

	private final Context context;

	private FormatOnSaveTask(Context context) {
		this.context = context;
	}

	public SortedSet<Pair<Integer, Integer>> getChangedLines(Context context1, StyledDocument doc) {
		final SortedSet<Pair<Integer, Integer>> changedElements = new TreeSet<>();
		Element root = context1.getModificationsRootElement();
		for (int i = 0; i < root.getElementCount(); i++) {
			Element e = root.getElement(i);
			int startOffset = e.getStartOffset();
			int endOffset = e.getEndOffset();

			int startLine = NbDocument.findLineNumber(doc, startOffset);
			int endLine = NbDocument.findLineNumber(doc, endOffset);
			// format at least one line
			if (startLine == endLine) {
				endLine = startLine + 1;
			}
			int start = NbDocument.findLineOffset(doc, startLine) - 1;
			int end = NbDocument.findLineOffset(doc, endLine);

			try {
				LOG.finest(String.format("Offset %s-%s -> Line %s-%s -> Offset %s-%s", startOffset, endOffset, startLine, endLine, start, end));
				LOG.finest("\n\"" + doc.getText(start, end - start) + "\"\n");
			} catch (BadLocationException ex) {
				Exceptions.printStackTrace(ex);
			}

			changedElements.add(Pair.of(start, end));
		}
		return changedElements;
	}

	@Override
	public void performTask() {
		final StyledDocument styledDoc = (StyledDocument) this.context.getDocument();
		Preferences pref = getActivePreferences(styledDoc);

		final boolean enableSaveAction = pref.getBoolean(ENABLE_SAVEACTION, false);
		final boolean modifiedLinesOnly = pref.getBoolean(ENABLE_SAVEACTION_MODIFIEDLINESONLY, false);
		if (enableSaveAction) {
			JTextComponent editor = EditorRegistry.lastFocusedComponent();
			int caret = (null != editor) ? editor.getCaretPosition() : -1;
			final boolean isSaveAction = true;
			SortedSet<Pair<Integer, Integer>> changedElements = null;
			if (modifiedLinesOnly && FEATURE_formatChangedLinesOnly) {
				changedElements = getChangedLines(context, styledDoc);
			}

			ParameterObject po = new ParameterObject();
			po.styledDoc = styledDoc;
			po.changedElements = changedElements;
			po.forSave = isSaveAction;
			po.selectionStart = -1;
			po.selectionEnd = -1;
			po.caret = caret;
			po.editor = editor;

			new FormatterStrategyDispatcher().format(po);
		}
	}

	@Override
	public void runLocked(Runnable run) {
		run.run();
	}

	@Override
	public boolean cancel() {
		return true;
	}

	@MimeRegistration(mimeType = "text/x-java", service = OnSaveTask.Factory.class, position = 1500)
	public static final class FactoryImpl implements Factory {
		@Override
		public OnSaveTask createTask(Context context) {
			return new FormatOnSaveTask(context);
		}
	}
}
