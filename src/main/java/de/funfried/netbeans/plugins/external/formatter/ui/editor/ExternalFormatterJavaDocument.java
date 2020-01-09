/*
 * Copyright (c) 2019 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui.editor;

import java.beans.PropertyChangeSupport;
import java.util.Dictionary;
import java.util.Objects;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.java.ui.FmtOptions;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

import de.funfried.netbeans.plugins.external.formatter.strategies.FormatterStrategyDispatcher;
import de.funfried.netbeans.plugins.external.formatter.ui.options.ExternalFormatterPreferencesChangeSupport;

/**
 *
 * @author bahlef
 */
public class ExternalFormatterJavaDocument extends NbEditorDocument {
	private static final Logger log = Logger.getLogger(ExternalFormatterJavaDocument.class.getName());

	private final ChangeListener cl = (ChangeEvent e) -> {
		((CustomDocumentProperties) getDocumentProperties()).firePropertyChange(null, false, true);
	};

	public ExternalFormatterJavaDocument() {
		super(JavaTokenId.language().mimeType());

		ExternalFormatterPreferencesChangeSupport changeSupport = Lookup.getDefault().lookup(ExternalFormatterPreferencesChangeSupport.class);
		if (changeSupport != null) {
			changeSupport.addChangeListener(WeakListeners.change(cl, changeSupport));
		} else {
			log.warning("Could not find ExternalFormatterPreferencesChangeSupport in lookup!");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dictionary<Object, Object> getDocumentProperties() {
		Dictionary<Object, Object> documentProperties = super.getDocumentProperties();
		if (!(documentProperties instanceof CustomDocumentProperties)) {
			documentProperties = new CustomDocumentProperties(documentProperties);

			setDocumentProperties(documentProperties);
		}

		return documentProperties;
	}

	private class CustomDocumentProperties extends LazyPropertyMap {
		private PropertyChangeSupport pcs = null;

		public CustomDocumentProperties(Dictionary<Object, Object> original) {
			super(original);

			for (Object key : this.keySet()) {
				if (key == PropertyChangeSupport.class) {
					Object value = get(key);
					if (value instanceof PropertyChangeSupport) {
						pcs = (PropertyChangeSupport) value;
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized Object get(Object key) {
			if (Objects.equals(FmtOptions.rightMargin, key)) {
				Integer rightMargin = FormatterStrategyDispatcher.getInstance().getRightMargin(ExternalFormatterJavaDocument.this);
				if (rightMargin != null) {
					return rightMargin;
				}
			} else if (Objects.equals(FmtOptions.expandTabToSpaces, key)) {
				Boolean expandTabToSpaces = FormatterStrategyDispatcher.getInstance().isExpandTabToSpaces(ExternalFormatterJavaDocument.this);
				if (expandTabToSpaces != null) {
					return expandTabToSpaces;
				}
			} else if (Objects.equals(FmtOptions.spacesPerTab, key)) {
				Integer spacesPerTab = FormatterStrategyDispatcher.getInstance().getSpacesPerTab(ExternalFormatterJavaDocument.this);
				if (spacesPerTab != null) {
					return spacesPerTab;
				}
			} else if (Objects.equals(FmtOptions.tabSize, key)) {
				Integer tabSize = FormatterStrategyDispatcher.getInstance().getSpacesPerTab(ExternalFormatterJavaDocument.this);
				if (tabSize != null) {
					return tabSize;
				}
			} else if (Objects.equals(FmtOptions.indentSize, key)) {
				Integer indentSize = FormatterStrategyDispatcher.getInstance().getIndentSize(ExternalFormatterJavaDocument.this);
				if (indentSize != null) {
					return indentSize;
				}
			} else if (Objects.equals(FmtOptions.continuationIndentSize, key)) {
				Integer continuationIndentSize = FormatterStrategyDispatcher.getInstance().getContinuationIndentSize(ExternalFormatterJavaDocument.this);
				if (continuationIndentSize != null) {
					return continuationIndentSize;
				}
			}

			return super.get(key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object put(Object key, Object value) {
			if (key == PropertyChangeSupport.class && value instanceof PropertyChangeSupport) {
				pcs = (PropertyChangeSupport) value;
			}

			return super.put(key, value);
		}

		private void firePropertyChange(String key, Object oldValue, Object newValue) {
			if (pcs != null) {
				pcs.firePropertyChange(key, oldValue, newValue);
			}
		}
	}
}
