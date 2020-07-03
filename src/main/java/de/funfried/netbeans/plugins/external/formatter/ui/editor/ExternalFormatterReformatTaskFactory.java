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
import java.util.Map;
import java.util.WeakHashMap;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.ReformatTask;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;

import de.funfried.netbeans.plugins.external.formatter.FormatterServiceDelegate;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.ui.Icons;
import de.funfried.netbeans.plugins.external.formatter.ui.options.Settings;

/**
 * Custom implementation of the {@link ReformatTask.Factory} which delegates the formatting
 * tasks to the configured external formatter or to the internal NetBeans formatter.
 *
 * @author bahlef
 */
public class ExternalFormatterReformatTaskFactory implements ReformatTask.Factory {
	/** {@link Map} which acts as a cache for default implementations of the {@link ReformatTask.Factory}. */
	private static final Map<MimePath, Reference<ReformatTask.Factory>> cache = new WeakHashMap<MimePath, Reference<ReformatTask.Factory>>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReformatTask createTask(Context context) {
		Document document = context.document();

		ReformatTask.Factory netbeansDefaultFactory = getDefaultForMimePath(context.mimePath());
		ReformatTask netbeansDefaultTask = netbeansDefaultFactory.createTask(context);

		MimeType mimeType = MimeType.getMimeType(document);
		if (mimeType != null) {
			Preferences prefs = Settings.getActivePreferences(document);
			if (Settings.DEFAULT_FORMATTER.equals(prefs.get(Settings.ENABLED_FORMATTER_PREFIX + mimeType.toString(), Settings.DEFAULT_FORMATTER))) {
				ReformatTask wrapper = new ReformatTask() {
					/**
					 * {@inheritDoc}
					 */
					@Override
					public void reformat() throws BadLocationException {
						formatWithNetBeansFormatter(netbeansDefaultTask, document);
					}

					/**
					 * {@inheritDoc}
					 */
					@Override
					public ExtraLock reformatLock() {
						return netbeansDefaultTask.reformatLock();
					}
				};

				return wrapper;
			}
		}

		return new ReformatTask() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void reformat() throws BadLocationException {
				if (!FormatterServiceDelegate.getInstance().format(EditorUtils.toStyledDocument(document), EditorUtils.getChangedElements(context))) {
					formatWithNetBeansFormatter(netbeansDefaultTask, document);
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

	/**
	 * Returns the cached default implementation of {@link ReformatTask.Factory}
	 * for the given {@code mimePath}.
	 *
	 * @param mimePath the mime path for which to get the {@link ReformatTask.Factory}
	 *
	 * @return the cached default implementation of {@link ReformatTask.Factory}
	 *         for the given {@code mimePath}
	 */
	@NonNull
	private ReformatTask.Factory getDefaultForMimePath(String mimePath) {
		MimePath mp = MimePath.get(mimePath);
		Reference<ReformatTask.Factory> ref = cache.get(mp);
		ReformatTask.Factory factory = ref == null ? null : ref.get();
		if (factory == null) {
			Collection<? extends ReformatTask.Factory> reformatTasks = MimeLookup.getLookup(mp).lookupAll(ReformatTask.Factory.class);
			for (ReformatTask.Factory rtf : reformatTasks) {
				if (!this.equals(rtf)) {
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

	private void formatWithNetBeansFormatter(ReformatTask netbeansDefaultTask, Document document) throws BadLocationException {
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
