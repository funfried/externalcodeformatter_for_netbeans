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
package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.TextEdit;

import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.strategies.eclipse.xml.ConfigReadException;

public final class EclipseFormatter {
	private static final Logger log = Logger.getLogger(EclipseFormatter.class.getName());

	private static final int FORMATTER_OPTS = CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS /* + CodeFormatter.K_CLASS_BODY_DECLARATIONS + CodeFormatter.K_STATEMENTS */;

	EclipseFormatter() {
	}

	public String format(String formatterFile, String formatterProfile, String code, String lineFeed, String sourceLevel, SortedSet<Pair<Integer, Integer>> changedElements)
			throws ConfigReadException, ProfileNotFoundException, CannotLoadConfigurationException {
		if (code == null) {
			return null;
		}

		Map<String, String> allConfig = EclipseFormatterConfig.parseConfig(formatterFile, formatterProfile, sourceLevel);

		CodeFormatter formatter = ToolFactory.createCodeFormatter(allConfig, ToolFactory.M_FORMAT_EXISTING);
		//see http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fcore%2Fformatter%2FCodeFormatter.html&anchor=format(int,

		List<IRegion> regions = new ArrayList<>();
		if (!CollectionUtils.isEmpty(changedElements)) {
			for (Pair<Integer, Integer> changedElement : changedElements) {
				regions.add(new Region(changedElement.getLeft(), (changedElement.getRight() - changedElement.getLeft()) + 1));
			}
		} else {
			regions.add(new Region(0, code.length()));
		}

		return format(formatter, code, regions.toArray(new IRegion[regions.size()]), lineFeed);
	}

	private String format(CodeFormatter formatter, String code, IRegion[] regions, String lineFeed) {
		String formattedCode = null;

		TextEdit te = formatter.format(FORMATTER_OPTS, code, regions, 0, lineFeed);
		if (te != null && te.getChildrenSize() > 0) {
			try {
				IDocument dc = new Document(code);
				te.apply(dc);

				formattedCode = dc.get();

				if (Objects.equals(code, formattedCode)) {
					return null;
				}
			} catch (Exception ex) {
				log.log(Level.WARNING, "Code could not be formatted!", ex);
				return null;
			}
		}

		return formattedCode;
	}
}
