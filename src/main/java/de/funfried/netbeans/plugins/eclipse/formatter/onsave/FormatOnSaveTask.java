/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * Saad Mufti <saad.mufti@teamaol.com>
 */
package de.funfried.netbeans.plugins.eclipse.formatter.onsave;

import de.funfried.netbeans.plugins.eclipse.formatter.strategies.ParameterObject;
import de.funfried.netbeans.plugins.eclipse.formatter.strategies.FormatterStrategyDispatcher;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
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

import de.funfried.netbeans.plugins.eclipse.formatter.options.Settings;

public class FormatOnSaveTask implements OnSaveTask {
	private static final Logger log = Logger.getLogger(FormatOnSaveTask.class.getName());

	private final ReentrantLock lock = new ReentrantLock();

	private final Context context;

	private FormatOnSaveTask(Context context) {
		this.context = context;
	}

	public SortedSet<Pair<Integer, Integer>> getChangedLines(StyledDocument doc) {
		final SortedSet<Pair<Integer, Integer>> changedElements = new TreeSet<>();
		Element root = context.getModificationsRootElement();
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
				log.finest(String.format("Offset %s-%s -> Line %s-%s -> Offset %s-%s", startOffset, endOffset, startLine, endLine, start, end));
				log.log(Level.FINEST, "\n\"{0}\"\n", doc.getText(start, end - start));
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
		Preferences pref = Settings.getActivePreferences(styledDoc);

		final boolean enableSaveAction = pref.getBoolean(Settings.ENABLE_SAVEACTION, false);
		if (enableSaveAction) {
			SortedSet<Pair<Integer, Integer>> changedElements = null;

			final boolean modifiedLinesOnly = pref.getBoolean(Settings.ENABLE_SAVEACTION_MODIFIEDLINESONLY, false);
			if (modifiedLinesOnly && Settings.FEATURE_FORMAT_CHANGED_LINES_ONLY) {
				changedElements = getChangedLines(styledDoc);
			}

			JTextComponent editor = EditorRegistry.findComponent(styledDoc);
			int caret = (null != editor) ? editor.getCaretPosition() : -1;

			ParameterObject po = new ParameterObject();
			po.styledDoc = styledDoc;
			po.changedElements = changedElements;
			po.forSave = true;
			po.selectionStart = -1;
			po.selectionEnd = -1;
			po.caret = caret;
			po.editor = editor;

			FormatterStrategyDispatcher.getInstance().format(po);
		}
	}

	@Override
	public void runLocked(Runnable run) {
		if (run != null) {
			lock.lock();

			try {
				run.run();
			} finally {
				lock.unlock();
			}
		}
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
