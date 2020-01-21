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
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.ReformatTask;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.strategies.FormatterAdvice;
import de.funfried.netbeans.plugins.external.formatter.strategies.FormatterStrategyDispatcher;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 *
 * @author bahlef
 */
public class ExternalFormatterTaskFactory implements ReformatTask.Factory {
	private static final Logger log = Logger.getLogger(ExternalFormatterTaskFactory.class.getName());

	private static final Map<MimePath, Reference<ReformatTask.Factory>> cache = new WeakHashMap<MimePath, Reference<ReformatTask.Factory>>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReformatTask createTask(Context context) {
		Document document = context.document();

		ReformatTask.Factory netbeansDefaultFactory = getDefaultForMimePath(context.mimePath());
		ReformatTask netbeansDefaultTask = netbeansDefaultFactory.createTask(context);

		Preferences prefs = Settings.getActivePreferences(document);
		if (Settings.DEFAULT_FORMATTER.equals(prefs.get(Settings.ENABLED_FORMATTER, Settings.DEFAULT_FORMATTER))) {
			ReformatTask wrapper = new ReformatTask() {
				@Override
				public void reformat() throws BadLocationException {
					netbeansDefaultTask.reformat();

					Preferences pref = Settings.getActivePreferences(document);

					SwingUtilities.invokeLater(() -> {
						if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
							NotificationDisplayer.getDefault().notify("Format using NetBeans formatter", Icons.ICON_NETBEANS, null, null);
						}

						StatusDisplayer.getDefault().setStatusText("Format using NetBeans formatter");
					});
				}

				@Override
				public ExtraLock reformatLock() {
					return netbeansDefaultTask.reformatLock();
				}
			};

			return wrapper;
		}

		return new ReformatTask() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void reformat() throws BadLocationException {
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

				if (!FormatterStrategyDispatcher.getInstance().format(new FormatterAdvice((StyledDocument) document, changedElements))) {
					netbeansDefaultTask.reformat();

					Preferences pref = Settings.getActivePreferences(document);

					SwingUtilities.invokeLater(() -> {
						if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
							NotificationDisplayer.getDefault().notify("Format using NetBeans formatter", Icons.ICON_NETBEANS, null, null);
						}

						StatusDisplayer.getDefault().setStatusText("Format using NetBeans formatter");
					});
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public ExtraLock reformatLock() {
				return netbeansDefaultTask.reformatLock();
			}
		};

	}

	private ReformatTask.Factory getDefaultForMimePath(String mimePath) {
		MimePath mp = MimePath.get(mimePath);
		Reference<ReformatTask.Factory> ref = cache.get(mp);
		ReformatTask.Factory factory = ref == null ? null : ref.get();
		if (factory == null) {
			Collection<? extends ReformatTask.Factory> reformatTasks = MimeLookup.getLookup(mp).lookupAll(ReformatTask.Factory.class);
			for (ReformatTask.Factory rtf : reformatTasks) {
				if (!ExternalFormatterTaskFactory.this.equals(rtf)) {
					factory = rtf;

					break;
				}
			}

			if (factory == null) {
				throw new IllegalStateException("Could not find NetBeans default implementation of ReformatTask for mime path '" + mimePath + "'");
			}

			cache.put(mp, new WeakReference<>(factory));
		}

		return factory;
	}
}
