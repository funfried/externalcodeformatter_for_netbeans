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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

import de.funfried.netbeans.plugins.external.formatter.FormatterServiceDelegate;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Custom implementation of the {@link IndentTask.Factory} which delegates the indenting
 * tasks to the configured external formatter or to the internal NetBeans formatter.
 *
 * @author bahlef
 */
public class ExternalFormatterIndentTaskFactory implements IndentTask.Factory {
	/** {@link Map} which acts as a cache for default implementations of the {@link IndentTask.Factory}. */
	private static final Map<MimePath, Reference<IndentTask.Factory>> cache = new WeakHashMap<MimePath, Reference<IndentTask.Factory>>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndentTask createTask(Context context) {
		Document document = context.document();

		IndentTask.Factory netbeansDefaultFactory = getDefaultForMimePath(context.mimePath());
		IndentTask netbeansDefaultTask = netbeansDefaultFactory.createTask(context);

		MimeType mimeType = MimeType.getByMimeType(NbEditorUtilities.getMimeType(document));
		if (mimeType != null) {
			Preferences prefs = Settings.getActivePreferences(document);
			if (Settings.DEFAULT_FORMATTER.equals(prefs.get(Settings.ENABLED_FORMATTER_PREFIX + mimeType.toString(), Settings.DEFAULT_FORMATTER))) {
				IndentTask wrapper = new IndentTask() {
					/**
					 * {@inheritDoc}
					 */
					@Override
					public void reindent() throws BadLocationException {
						formatWithNetBeansFormatter(netbeansDefaultTask, document);
					}

					/**
					 * {@inheritDoc}
					 */
					@Override
					public ExtraLock indentLock() {
						return netbeansDefaultTask.indentLock();
					}
				};

				return wrapper;
			}
		}

		return new IndentTask() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void reindent() throws BadLocationException {
				SortedSet<Pair<Integer, Integer>> changedElements = new TreeSet<>();

				int documentLength = document.getLength();

				List<Context.Region> regions = context.indentRegions();
				for (Context.Region region : regions) {
					int start = region.getStartOffset();
					if (start < 0) {
						continue;
					}

					int end = region.getEndOffset();
					if (end >= documentLength) {
						end = documentLength - 1;
					}

					changedElements.add(Pair.of(start, end));
				}

				StyledDocument styledDocument = null;

				if (document instanceof StyledDocument) {
					styledDocument = (StyledDocument) document;
				} else {
					DataObject dataObject = NbEditorUtilities.getDataObject(document);
					if (dataObject != null) {
						styledDocument = NbDocument.getDocument(dataObject);
					}
				}

				if (!FormatterServiceDelegate.getInstance().format(styledDocument, changedElements)) {
					formatWithNetBeansFormatter(netbeansDefaultTask, document);
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public ExtraLock indentLock() {
				return netbeansDefaultTask.indentLock();
			}
		};
	}

	/**
	 * Returns the cached default implementation of {@link IndentTask.Factory}
	 * for the given {@code mimePath}.
	 *
	 * @param mimePath the mime path for which to get the {@link IndentTask.Factory}
	 *
	 * @return the cached default implementation of {@link IndentTask.Factory}
	 *         for the given {@code mimePath}
	 */
	@NonNull
	private IndentTask.Factory getDefaultForMimePath(String mimePath) {
		MimePath mp = MimePath.get(mimePath);
		Reference<IndentTask.Factory> ref = cache.get(mp);
		IndentTask.Factory factory = ref == null ? null : ref.get();
		if (factory == null) {
			Collection<? extends IndentTask.Factory> indentTasks = MimeLookup.getLookup(mp).lookupAll(IndentTask.Factory.class);
			for (IndentTask.Factory itf : indentTasks) {
				if (!this.equals(itf)) {
					factory = itf;

					break;
				}
			}

			if (factory == null) {
				throw new IllegalStateException("Could not find NetBeans default implementation of IndentTask for mime path '" + mimePath + "'");
			}

			cache.put(mp, new WeakReference<>(factory));
		}

		return factory;
	}

	private void formatWithNetBeansFormatter(IndentTask netbeansDefaultTask, Document document) throws BadLocationException {
		netbeansDefaultTask.reindent();

		Preferences pref = Settings.getActivePreferences(document);

		SwingUtilities.invokeLater(() -> {
			if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
				NotificationDisplayer.getDefault().notify("Format using NetBeans formatter", Icons.ICON_NETBEANS, null, null);
			}

			StatusDisplayer.getDefault().setStatusText("Format using NetBeans formatter");
		});
	}
}
