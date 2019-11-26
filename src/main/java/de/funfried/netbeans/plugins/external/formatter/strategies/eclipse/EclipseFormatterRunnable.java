/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.eclipse;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

import de.funfried.netbeans.plugins.external.formatter.Utils;
import de.funfried.netbeans.plugins.external.formatter.exceptions.CannotLoadConfigurationException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.FileTypeNotSupportedException;
import de.funfried.netbeans.plugins.external.formatter.exceptions.ProfileNotFoundException;
import de.funfried.netbeans.plugins.external.formatter.options.Settings;
import de.funfried.netbeans.plugins.external.formatter.strategies.AbstractFormatterRunnable;

/**
 * Formats the given document using the eclipse formatter. LineBreakpoints get
 * removed and the following breakpoints are getting reattached:
 * <ul>
 * <li>ClassLoadUnloadBreakpoint</li>
 * <li>FieldBreakpoint</li>
 * <li>MethodBreakpoint</li>
 * </ul>
 */
class EclipseFormatterRunnable extends AbstractFormatterRunnable {
	private static final Logger log = Logger.getLogger(EclipseFormatterRunnable.class.getName());

	private final EclipseFormatter formatter;

	EclipseFormatterRunnable(StyledDocument document, EclipseFormatter formatter, int dot, int mark, SortedSet<Pair<Integer, Integer>> changedElements) {
		super(document, dot, mark, changedElements);

		this.formatter = formatter;
	}

	@Override
	public void run() {
		boolean isJava = Utils.isJava(document);
		if (!isJava) {
			throw new FileTypeNotSupportedException("The file type '" + NbEditorUtilities.getMimeType(document) + "' is not supported by the Eclipse Java Code Formatter");
		}

		Preferences pref = Settings.getActivePreferences(document);

		String formatterFilePref = getFormatterFileFromProjectConfiguration(pref.getBoolean(Settings.USE_PROJECT_PREFS, true), document);
		if (null == formatterFilePref) {
			formatterFilePref = pref.get(Settings.ECLIPSE_FORMATTER_LOCATION, null);
		}

		final String formatterFile = formatterFilePref;
		final String formatterProfile = pref.get(Settings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
		String lineFeedSetting = pref.get(Settings.LINEFEED, "");
		String sourceLevel = pref.get(Settings.SOURCELEVEL, "");
		boolean preserveBreakpoints = pref.getBoolean(Settings.PRESERVE_BREAKPOINTS, true);

		//save with configured linefeed
		String lineFeed = Settings.getLineFeed(lineFeedSetting, System.getProperty("line.separator"));
		if (null != lineFeedSetting) {
			document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, lineFeed);
			document.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP, lineFeed);
		}

		final String code;

		try {
			code = document.getText(0, document.getLength());
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
			return;
		}

		try {
			SortedSet<Pair<Integer, Integer>> regions = changedElements;
			if (CollectionUtils.isEmpty(changedElements)) {
				regions = new TreeSet<>();

				if (this.startOffset > -1 && this.endOffset > -1) {
					regions.add(Pair.of(this.startOffset, this.endOffset));
				} else {
					regions.add(Pair.of(0, code.length() - 1));
				}
			}

			GuardedSectionManager guards = GuardedSectionManager.getInstance(document);
			if (guards != null) {
				SortedSet<Pair<Integer, Integer>> nonGuardedSections = new TreeSet<>();
				Iterable<GuardedSection> guardedSections = guards.getGuardedSections();

				StringBuilder sb = new StringBuilder();
				guardedSections.forEach(guard -> sb.append(guard.getStartPosition().getOffset()).append("/").append(guard.getEndPosition().getOffset()).append(" "));
				log.log(Level.FINEST, "Guarded sections: {0}", sb.toString().trim());

				for (Pair<Integer, Integer> changedElement : regions) {
					nonGuardedSections.addAll(avoidGuardedSection(changedElement, guardedSections));
				}

				regions = nonGuardedSections;
			}

			final List<Pair<Integer, Integer>> regionsList = regions.stream().collect(Collectors.toList());

			StringBuilder sb = new StringBuilder();
			regionsList.stream().forEach(section -> sb.append(section.getLeft()).append("/").append(section.getRight()).append(" "));
			log.log(Level.FINEST, "Formating sections: {0}", sb.toString().trim());

			String formattedContent = formatter.format(formatterFile, formatterProfile, code, lineFeed, sourceLevel, regions);
			// quick check for changed
			if (setFormattedCode(code, formattedContent, guards, preserveBreakpoints)) {
				String msg = getNotificationMessageForEclipseFormatterConfigurationFileType(formatterFile, formatterProfile);

				SwingUtilities.invokeLater(() -> {
					if (pref.getBoolean(Settings.SHOW_NOTIFICATIONS, false)) {
						NotificationDisplayer.getDefault().notify("Format using Eclipse formatter", Utils.iconEclipse, msg, null);
					}

					StatusDisplayer.getDefault().setStatusText("Format using Eclipse formatter: " + msg);
				});
			}
		} catch (ProfileNotFoundException ex) {
			SwingUtilities.invokeLater(() -> {
				NotifyDescriptor notify = new NotifyDescriptor.Message(
						String.format("<html>Profile '%s' not found in <tt>%s</tt><br><br>Please configure a valid one in the project properties OR at Tools|Options|Java|Eclipse Formatter!", formatterProfile,
								formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);
			});

			throw ex;
		} catch (CannotLoadConfigurationException ex) {
			SwingUtilities.invokeLater(() -> {
				NotifyDescriptor notify = new NotifyDescriptor.Message(String.format("<html>Could not find configuration file %s.<br>Make sure the file exists and it can be read.", formatterFile),
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(notify);
			});

			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		}
	}

	private String getFormatterFileFromProjectConfiguration(final boolean useProjectPrefs, final StyledDocument styledDoc) {
		//use ${projectdir}/.settings/org.eclipse.jdt.core.prefs, if activated in options
		if (useProjectPrefs) {
			FileObject fileForDocument = NbEditorUtilities.getFileObject(styledDoc);
			if (null != fileForDocument) {

				Project project = FileOwnerQuery.getOwner(fileForDocument);
				if (null != project) {
					FileObject projectDirectory = project.getProjectDirectory();
					FileObject preferenceFile = projectDirectory.getFileObject(".settings/" + Settings.PROJECT_PREF_FILE);
					if (null != preferenceFile) {
						return preferenceFile.getPath();
					}
				}
			}
		}
		return null;
	}

	private String getNotificationMessageForEclipseFormatterConfigurationFileType(String formatterFile, String formatterProfile) {
		String msg = "";
		if (Settings.isWorkspaceMechanicFile(formatterFile)) {
			//Workspace mechanic file
			msg = String.format("Using %s", formatterFile);
		} else if (Settings.isXMLConfigurationFile(formatterFile)) {
			//XML file
			msg = String.format("Using profile '%s' from %s", formatterProfile, formatterFile);
		} else if (Settings.isProjectSetting(formatterFile)) {
			//org.eclipse.jdt.core.prefs
			msg = String.format("Using %s", formatterFile);
		}
		return msg;
	}
}
