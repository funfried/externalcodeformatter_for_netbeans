/*
 * Copyright (c) 2022 alexander.kronenwett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * alexander.kronenwett - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter.eclipse.mechanic;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WorkspaceMechanicConfigParserTest {
	private static final String PREFIX = "/instance/org.eclipse.jdt.core/";

	@Rule
	public TemporaryFolder EPF_FOLDER = new TemporaryFolder();

	/**
	 * Contains the actual epf files.
	 */
	@Rule
	public TemporaryFolder EPF_PROXY_FOLDER = new TemporaryFolder();

	@Test
	public void testSimpleEpfFileFiltered() throws Exception {
		File epfImportFile = EPF_FOLDER.newFile("imports.epf");
		try (FileWriter fw = new FileWriter(epfImportFile)) {
			fw.write("""
					/instance/org.eclipse.jdt.core/importorder=java;javax;org;com;
					/instance/org.eclipse.jdt.core/ondemandthreshold=99
					/instance/org.eclipse.jdt.core/staticondemandthreshold=2
					/instance/org.eclipse.jdt.ui/staticondemandthreshold=7""");
		}

		Map<String, String> props = WorkspaceMechanicConfigParser.readPropertiesFromConfiguration(epfImportFile.getAbsolutePath(), PREFIX);

		Assert.assertEquals(3, props.size());
		Assert.assertEquals("2", props.get("staticondemandthreshold"));
		Assert.assertEquals("99", props.get("ondemandthreshold"));
		Assert.assertEquals("java;javax;org;com;", props.get("importorder"));
	}

	@Test
	public void testSimpleEpfFileUnfiltered() throws Exception {
		File epfImportFile = EPF_FOLDER.newFile("imports.epf");
		try (FileWriter fw = new FileWriter(epfImportFile)) {
			fw.write("""
					/instance/org.eclipse.jdt.core/importorder=java;javax;org;com;
					/instance/org.eclipse.jdt.core/ondemandthreshold=99
					/instance/org.eclipse.jdt.core/staticondemandthreshold=2
					/instance/org.eclipse.jdt.ui/adifferentpref=7""");
		}

		Map<String, String> props = WorkspaceMechanicConfigParser.readPropertiesFromConfiguration(epfImportFile.getAbsolutePath(), null);

		Assert.assertEquals(4, props.size());
		Assert.assertEquals("2", props.get("/instance/org.eclipse.jdt.core/staticondemandthreshold"));
		Assert.assertEquals("99", props.get("/instance/org.eclipse.jdt.core/ondemandthreshold"));
		Assert.assertEquals("java;javax;org;com;", props.get("/instance/org.eclipse.jdt.core/importorder"));
		Assert.assertEquals("7", props.get("/instance/org.eclipse.jdt.ui/adifferentpref"));
	}

	@Test
	public void testProxyEpfFile() throws Exception {
		String epfProxyFolderPath = EPF_PROXY_FOLDER.getRoot().getAbsolutePath();
		if (SystemUtils.IS_OS_WINDOWS) {
			epfProxyFolderPath = StringUtils.replace(epfProxyFolderPath, "\\", "\\\\");
		}

		File epfFile = EPF_FOLDER.newFile();
		try (FileWriter fw = new FileWriter(epfFile)) {
			fw.write(
					"/instance/com.google.eclipse.mechanic/mechanicSourceDirectories=[\"" + epfProxyFolderPath + "\",\"/a/user/path/.eclipse/mechanic\"]");
		}

		File epfImportFile = EPF_PROXY_FOLDER.newFile("imports.epf");
		try (FileWriter fw = new FileWriter(epfImportFile)) {
			fw.write("""
					/instance/org.eclipse.jdt.core/importorder=java;javax;org;com;
					/instance/org.eclipse.jdt.core/ondemandthreshold=99
					/instance/org.eclipse.jdt.core/staticondemandthreshold=2""");
		}

		File epfSaveActionsFile = EPF_PROXY_FOLDER.newFile("onsave.epf");
		try (FileWriter fw = new FileWriter(epfSaveActionsFile)) {
			fw.write("""
					/instance/org.eclipse.jdt.core/sp_cleanup.format_source_code=true
					/instance/org.eclipse.jdt.core/sp_cleanup.on_save_use_additional_actions=true""");
		}

		Map<String, String> props = WorkspaceMechanicConfigParser.readPropertiesFromConfiguration(epfFile.getAbsolutePath(), PREFIX);

		Assert.assertEquals(5, props.size());
		Assert.assertEquals("2", props.get("staticondemandthreshold"));
		Assert.assertEquals("99", props.get("ondemandthreshold"));
		Assert.assertEquals("java;javax;org;com;", props.get("importorder"));
		Assert.assertEquals("true", props.get("sp_cleanup.format_source_code"));
		Assert.assertEquals("true", props.get("sp_cleanup.on_save_use_additional_actions"));
	}
}