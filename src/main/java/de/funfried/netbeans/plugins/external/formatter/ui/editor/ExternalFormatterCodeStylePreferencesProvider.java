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

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.modules.editor.indent.spi.CodeStylePreferences;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

import de.funfried.netbeans.plugins.external.formatter.FormatterServiceDelegate;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.ui.options.ExternalFormatterPreferencesChangeSupport;

/**
 * Implementation of the {@link CodeStylePreferences.Provider} to provide custom properties to the NetBeans editor.
 *
 * @author bahlef
 */
@ServiceProvider(service = CodeStylePreferences.Provider.class, position = 1)
public class ExternalFormatterCodeStylePreferencesProvider implements CodeStylePreferences.Provider {
	private static final Map<String, Function<Document, String>> temporaryPreferenceProviders = new HashMap<>();

	private static final CodeStylePreferences.Provider defaultProvider = new CodeStylePreferences.Provider() {
		@Override
		public Preferences forFile(FileObject file, String mimeType) {
			Preferences prefs = MimeLookup.getLookup(mimeType).lookup(Preferences.class);
			if (prefs == null) {
				prefs = MimeLookup.getLookup(StringUtils.isBlank(mimeType) ? MimePath.EMPTY : MimePath.parse(mimeType)).lookup(Preferences.class);
			}

			return prefs;
		}

		@Override
		public Preferences forDocument(Document doc, String mimeType) {
			Preferences prefs = MimeLookup.getLookup(mimeType).lookup(Preferences.class);
			if (prefs == null) {
				prefs = MimeLookup.getLookup(StringUtils.isBlank(mimeType) ? MimePath.EMPTY : MimePath.parse(mimeType)).lookup(Preferences.class);
			}

			return prefs;
		}
	};

	static {
		temporaryPreferenceProviders.put(EditorConstants.TEXT_LIMIT_WIDTH, document -> Objects.toString(FormatterServiceDelegate.getInstance().getRightMargin(document), null));
		temporaryPreferenceProviders.put(EditorConstants.EXPAND_TABS, document -> Objects.toString(FormatterServiceDelegate.getInstance().isExpandTabToSpaces(document), null));
		temporaryPreferenceProviders.put(EditorConstants.SPACES_PER_TAB, document -> Objects.toString(FormatterServiceDelegate.getInstance().getSpacesPerTab(document), null));
		temporaryPreferenceProviders.put(EditorConstants.TAB_SIZE, document -> Objects.toString(FormatterServiceDelegate.getInstance().getSpacesPerTab(document), null));
		temporaryPreferenceProviders.put(EditorConstants.INDENT_SHIFT_WIDTH, document -> Objects.toString(FormatterServiceDelegate.getInstance().getIndentSize(document), null));
		temporaryPreferenceProviders.put(EditorConstants.CONTINUATION_INDENT_SIZE, document -> Objects.toString(FormatterServiceDelegate.getInstance().getContinuationIndentSize(document), null));
	}

	private final Map<Document, TemporaryDocumentPreferences> preferencesCache = new ConcurrentHashMap<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Preferences forFile(FileObject file, String mimeType) {
		if (StringUtils.isNotBlank(mimeType) && file != null) {
			Source source = Source.create(file);
			if (source != null) {
				Document document = source.getDocument(false);
				if (document != null) {
					return getTemporaryDocumentPreferences(document, mimeType);
				}
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Preferences forDocument(Document doc, String mimeType) {
		if (StringUtils.isNotBlank(mimeType) && doc != null) {
			return getTemporaryDocumentPreferences(doc, mimeType);
		}

		return null;
	}

	private TemporaryDocumentPreferences getTemporaryDocumentPreferences(Document doc, String mimeType) {
		if (MimeType.getByMimeType(mimeType) != null) {
			TemporaryDocumentPreferences tempDocPrefs = preferencesCache.get(doc);
			if (tempDocPrefs == null) {
				Collection<? extends CodeStylePreferences.Provider> providers = Lookup.getDefault().lookupAll(CodeStylePreferences.Provider.class);

				Preferences original = null;

				for (CodeStylePreferences.Provider p : providers) {
					if (p != null && !(p instanceof ExternalFormatterCodeStylePreferencesProvider)) {
						original = p.forDocument(doc, mimeType);
						if (original != null) {
							break;
						}
					}
				}

				if (original == null) {
					original = defaultProvider.forDocument(doc, mimeType);
				}

				tempDocPrefs = new TemporaryDocumentPreferences(doc, original, temporaryPreferenceProviders);

				preferencesCache.put(doc, tempDocPrefs);
			}

			return tempDocPrefs;
		}

		return null;
	}

	private static class TemporaryDocumentPreferences extends Preferences {
		/** {@link Logger} of this class. */
		private static final Logger log = Logger.getLogger(TemporaryDocumentPreferences.class.getName());

		private final ChangeListener cl = new ChangeListener() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void stateChanged(ChangeEvent e) {
				Runnable run = () -> {
					updateDocumentState();
				};

				if (SwingUtilities.isEventDispatchThread()) {
					run.run();
				} else {
					SwingUtilities.invokeLater(run);
				}
			}

			private void updateDocumentState() {
				String tabSize = Objects.toString(FormatterServiceDelegate.getInstance().getSpacesPerTab(document));

				firePreferenceChanged(EditorConstants.TEXT_LIMIT_WIDTH, Objects.toString(FormatterServiceDelegate.getInstance().getRightMargin(document)));
				firePreferenceChanged(EditorConstants.EXPAND_TABS, Objects.toString(FormatterServiceDelegate.getInstance().isExpandTabToSpaces(document)));
				firePreferenceChanged(EditorConstants.SPACES_PER_TAB, tabSize);
				firePreferenceChanged(EditorConstants.TAB_SIZE, tabSize);
				firePreferenceChanged(EditorConstants.INDENT_SHIFT_WIDTH, Objects.toString(FormatterServiceDelegate.getInstance().getIndentSize(document)));
				firePreferenceChanged(EditorConstants.CONTINUATION_INDENT_SIZE, Objects.toString(FormatterServiceDelegate.getInstance().getContinuationIndentSize(document)));

				if (document instanceof AbstractDocument) {
					AbstractDocument doc = (AbstractDocument) document;
					Dictionary<Object, Object> properties = doc.getDocumentProperties();
					Enumeration<Object> keys = properties.keys();
					while (keys.hasMoreElements()) {
						Object key = keys.nextElement();
						if (key == PropertyChangeSupport.class) {
							Object value = properties.get(key);
							if (value instanceof PropertyChangeSupport) {
								((PropertyChangeSupport) value).firePropertyChange(null, false, true);
							}
						}
					}
				}
			}
		};

		private final Map<String, Function<Document, String>> temporaryValueProviders;

		private final AtomicBoolean flush = new AtomicBoolean(false);

		private final Preferences delegate;

		private final Document document;

		private boolean noEnqueueMethodAvailable = false;

		public TemporaryDocumentPreferences(Document document, Preferences delegate, Map<String, Function<Document, String>> temporaryValueProviders) {
			this.temporaryValueProviders = temporaryValueProviders;
			this.delegate = delegate;
			this.document = document;

			ExternalFormatterPreferencesChangeSupport changeSupport = Lookup.getDefault().lookup(ExternalFormatterPreferencesChangeSupport.class);
			if (changeSupport != null) {
				changeSupport.addChangeListener(WeakListeners.change(cl, changeSupport));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void put(String key, String value) {
			delegate.put(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String get(String key, String def) {
			if (!this.flush.get()) {
				Function<Document, String> tempValueProvider = this.temporaryValueProviders.get(key);
				if (tempValueProvider != null) {
					String value = tempValueProvider.apply(this.document);
					if (value != null) {
						return value;
					}
				}
			}

			return delegate.get(key, def);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(String key) {
			delegate.remove(key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() throws BackingStoreException {
			delegate.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putInt(String key, int value) {
			delegate.putInt(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getInt(String key, int def) {
			return delegate.getInt(key, def);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putLong(String key, long value) {
			delegate.putLong(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long getLong(String key, long def) {
			return delegate.getLong(key, def);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putBoolean(String key, boolean value) {
			delegate.putBoolean(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean getBoolean(String key, boolean def) {
			return delegate.getBoolean(key, def);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putFloat(String key, float value) {
			delegate.putFloat(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getFloat(String key, float def) {
			return delegate.getFloat(key, def);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putDouble(String key, double value) {
			delegate.putDouble(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public double getDouble(String key, double def) {
			return delegate.getDouble(key, def);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putByteArray(String key, byte[] value) {
			delegate.putByteArray(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] getByteArray(String key, byte[] def) {
			return delegate.getByteArray(key, def);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String[] keys() throws BackingStoreException {
			return delegate.keys();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String[] childrenNames() throws BackingStoreException {
			return delegate.childrenNames();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Preferences parent() {
			return delegate.parent();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Preferences node(String pathName) {
			return delegate.node(pathName);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean nodeExists(String pathName) throws BackingStoreException {
			return delegate.nodeExists(pathName);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeNode() throws BackingStoreException {
			delegate.removeNode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			return delegate.name();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String absolutePath() {
			return delegate.absolutePath();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isUserNode() {
			return delegate.isUserNode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return delegate.toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void flush() throws BackingStoreException {
			this.flush.set(true);

			try {
				delegate.flush();
			} finally {
				this.flush.set(false);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void sync() throws BackingStoreException {
			this.flush.set(true);

			try {
				delegate.sync();
			} finally {
				this.flush.set(false);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
			delegate.addPreferenceChangeListener(pcl);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
			delegate.removePreferenceChangeListener(pcl);
		}

		protected void firePreferenceChanged(String key, String newValue) {
			if (!noEnqueueMethodAvailable) {
				try {
					Method enqueueMethod = AbstractPreferences.class.getDeclaredMethod("enqueuePreferenceChangeEvent", String.class, String.class); //NOI18N
					enqueueMethod.setAccessible(true);
					enqueueMethod.invoke(this.delegate, key, newValue);

					return;
				} catch (NoSuchMethodException ex) {
					noEnqueueMethodAvailable = true;
				} catch (Exception ex) {
					log.log(Level.WARNING, "runs into", ex);
				}
			}

			if (key != null && newValue != null) {
				remove(key);
				put(key, newValue);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addNodeChangeListener(NodeChangeListener ncl) {
			delegate.addNodeChangeListener(ncl);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeNodeChangeListener(NodeChangeListener ncl) {
			delegate.removeNodeChangeListener(ncl);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void exportNode(OutputStream os) throws IOException, BackingStoreException {
			delegate.exportNode(os);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
			delegate.exportSubtree(os);
		}
	}
}
