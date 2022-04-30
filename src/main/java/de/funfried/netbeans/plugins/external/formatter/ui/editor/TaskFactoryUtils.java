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

import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.editor.indent.spi.IndentTask;
import org.netbeans.modules.editor.indent.spi.ReformatTask;
import org.openide.util.Lookup;

/**
 * Custom implementation of the {@link IndentTask.Factory} which delegates the indenting
 * tasks to the configured external formatter or to the internal NetBeans formatter.
 *
 * @author bahlef
 */
public class TaskFactoryUtils {
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
	public static IndentTask.Factory getDefaultIndentTaskForMimePath(String mimePath) {
		ExternalFormatterIndentTaskFactory indentTaskFactory = Lookup.getDefault().lookup(ExternalFormatterIndentTaskFactory.class);
		if (indentTaskFactory != null) {
			return indentTaskFactory.getDefaultForMimePath(mimePath);
		}

		throw new IllegalStateException("Could not find NetBeans default implementation of IndentTask for mime path '" + mimePath + "'");
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
	public ReformatTask.Factory getDefaultReformatTaskForMimePath(String mimePath) {
		ExternalFormatterReformatTaskFactory reformatTaskFactory = Lookup.getDefault().lookup(ExternalFormatterReformatTaskFactory.class);
		if (reformatTaskFactory != null) {
			return reformatTaskFactory.getDefaultForMimePath(mimePath);
		}

		throw new IllegalStateException("Could not find NetBeans default implementation of IndentTask for mime path '" + mimePath + "'");
	}
}
