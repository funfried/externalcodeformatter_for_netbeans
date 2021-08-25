/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */

package de.funfried.netbeans.plugins.external.formatter.eclipse.xml;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author bahlef
 */
public class ConfigReaderTest {
	@Test
	public void testGetProfileNames() throws Exception {
		try {
			ConfigReader.getProfileNames(null);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("fileContent cannot be null", ex.getMessage());
		}

		String wrongProfileKindFileContent = ConfigReader.readContentFromFilePath("src/test/resources/wrongprofilekindsampleeclipse.xml");

		try {
			ConfigReader.getProfileNames(wrongProfileKindFileContent);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No valid <profile> tag found in given file content", ex.getMessage());
		}

		String defectFormatterFileContent = ConfigReader.readContentFromFilePath("src/test/resources/defectformattersampleeclipse.xml");

		try {
			ConfigReader.getProfileNames(defectFormatterFileContent);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profiles> tag found in config file", ex.getMessage());
		}

		String noProfileFileContent = ConfigReader.readContentFromFilePath("src/test/resources/noprofilesampleeclipse.xml");

		try {
			ConfigReader.getProfileNames(noProfileFileContent);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profile> tag found in given file content", ex.getMessage());
		}

		List<String> profileNames = ConfigReader.getProfileNames(ConfigReader.readContentFromFilePath("src/test/resources/formattersampleeclipse.xml"));

		Assert.assertFalse(profileNames.isEmpty());
		Assert.assertEquals(1, profileNames.size());
		Assert.assertEquals("eclipse-demo", profileNames.get(0));

		List<String> remoteProfileNames = ConfigReader
				.getProfileNames(ConfigReader.readContentFromFilePath("https://raw.githubusercontent.com/funfried/externalcodeformatter_for_netbeans/master/eclipse_formatter_template.xml"));

		Assert.assertFalse(remoteProfileNames.isEmpty());
		Assert.assertEquals(1, remoteProfileNames.size());
		Assert.assertEquals("EclipseCodeFormatterForNetBeans", remoteProfileNames.get(0));
	}

	@Test
	public void testGetProfileSettings() throws Exception {
		try {
			ConfigReader.getProfileSettings(null, "foobar");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("fileContent cannot be null", ex.getMessage());
		}

		String fileContent = ConfigReader.readContentFromFilePath("src/test/resources/formattersampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(fileContent, null);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Profile null not found in given file content", ex.getMessage());
		}

		try {
			ConfigReader.getProfileSettings(fileContent, "");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Profile  not found in given file content", ex.getMessage());
		}

		try {
			ConfigReader.getProfileSettings(fileContent, "foobar");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Profile foobar not found in given file content", ex.getMessage());
		}

		String wrongProfileKindFileContent = ConfigReader.readContentFromFilePath("src/test/resources/wrongprofilekindsampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(wrongProfileKindFileContent, "eclipse-demo");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No valid <profile> tag found in given file content", ex.getMessage());
		}

		String defectFormatterFileContent = ConfigReader.readContentFromFilePath("src/test/resources/defectformattersampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(defectFormatterFileContent, "eclipse-demo");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profiles> tag found in config file", ex.getMessage());
		}

		String noProfileFileContent = ConfigReader.readContentFromFilePath("src/test/resources/noprofilesampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(noProfileFileContent, "eclipse-demo");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profile> tag found in given file content", ex.getMessage());
		}

		Map<String, String> profileSettings = ConfigReader.getProfileSettings(fileContent, "eclipse-demo");

		Assert.assertFalse(profileSettings.isEmpty());
		Assert.assertEquals(1, profileSettings.size());
		Assert.assertEquals("70", profileSettings.get("org.eclipse.jdt.core.formatter.alignment_for_enum_constants"));

		Map<String, String> remoteProfileSettings = ConfigReader.getProfileSettings(
				ConfigReader.readContentFromFilePath("https://raw.githubusercontent.com/funfried/externalcodeformatter_for_netbeans/master/eclipse_formatter_template.xml"), "EclipseCodeFormatterForNetBeans");

		Assert.assertFalse(remoteProfileSettings.isEmpty());
		Assert.assertEquals(357, remoteProfileSettings.size());
		Assert.assertEquals("200", remoteProfileSettings.get("org.eclipse.jdt.core.formatter.comment.line_length"));

	}
}