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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openide.filesystems.FileObject;

/**
 *
 * @author bahlef
 */
public class ConfigReaderTest {

	public ConfigReaderTest() {
	}

	@Test
	public void testToFileObject_String() throws Exception {
		try {
			ConfigReader.toFileObject((String) null);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("File path cannot be empty", ex.getMessage());
		}

		String wrongFilename = "does_not_exist.file";
		File wrongFile = new File(wrongFilename);

		try {
			ConfigReader.toFileObject(wrongFilename);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Could not access file: " + wrongFile.getAbsolutePath(), ex.getMessage());
		}

		String filepath = "src/test/resources/";
		String filename = "formattersampleeclipse";
		String fileExt = "xml";
		String filenameExt = filename + "." + fileExt;

		FileObject fileObj = ConfigReader.toFileObject(filepath + filenameExt);

		Assert.assertNotNull(fileObj);
		Assert.assertEquals(filenameExt, fileObj.getNameExt());
		Assert.assertEquals(filename, fileObj.getName());
		Assert.assertEquals(fileExt, fileObj.getExt());
	}

	@Test
	public void testToFileObject_File() throws Exception {
		try {
			ConfigReader.toFileObject((File) null);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Could not access file: null", ex.getMessage());
		}

		String wrongFilename = "does_not_exist.file";
		File wrongFile = new File(wrongFilename);

		try {
			ConfigReader.toFileObject(wrongFile);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Could not access file: " + wrongFile.getAbsolutePath(), ex.getMessage());
		}

		String filepath = "src/test/resources/";
		String filename = "formattersampleeclipse";
		String fileExt = "xml";
		String filenameExt = filename + "." + fileExt;

		FileObject fileObj = ConfigReader.toFileObject(new File(filepath + filenameExt));

		Assert.assertNotNull(fileObj);
		Assert.assertEquals(filenameExt, fileObj.getNameExt());
		Assert.assertEquals(filename, fileObj.getName());
		Assert.assertEquals(fileExt, fileObj.getExt());
	}

	@Test
	public void testGetProfileSettings() throws Exception {
		try {
			ConfigReader.getProfileNames(null);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("FileObject cannot be null", ex.getMessage());
		}

		FileObject wrongProfileKindFileObject = ConfigReader.toFileObject("src/test/resources/wrongprofilekindsampleeclipse.xml");

		try {
			ConfigReader.getProfileNames(wrongProfileKindFileObject);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No valid <profile> tag found in " + wrongProfileKindFileObject.getPath(), ex.getMessage());
		}

		FileObject defectFormatterFileObject = ConfigReader.toFileObject("src/test/resources/defectformattersampleeclipse.xml");

		try {
			ConfigReader.getProfileNames(defectFormatterFileObject);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profiles> tag found in config file", ex.getMessage());
		}

		FileObject noProfileFileObject = ConfigReader.toFileObject("src/test/resources/noprofilesampleeclipse.xml");

		try {
			ConfigReader.getProfileNames(noProfileFileObject);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profile> tag found in " + noProfileFileObject.getPath(), ex.getMessage());
		}

		List<String> profileNames = ConfigReader.getProfileNames(ConfigReader.toFileObject("src/test/resources/formattersampleeclipse.xml"));

		Assert.assertFalse(profileNames.isEmpty());
		Assert.assertEquals(1, profileNames.size());
		Assert.assertEquals("eclipse-demo", profileNames.get(0));
	}

	@Test
	public void testGetProfileNames() throws Exception {
		try {
			ConfigReader.getProfileSettings(null, "foobar");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("FileObject cannot be null", ex.getMessage());
		}

		FileObject fileObject = ConfigReader.toFileObject("src/test/resources/formattersampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(fileObject, null);

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Profile null not found in " + fileObject.getPath(), ex.getMessage());
		}

		try {
			ConfigReader.getProfileSettings(fileObject, "");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Profile  not found in " + fileObject.getPath(), ex.getMessage());
		}

		try {
			ConfigReader.getProfileSettings(fileObject, "foobar");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("Profile foobar not found in " + fileObject.getPath(), ex.getMessage());
		}

		FileObject wrongProfileKindFileObject = ConfigReader.toFileObject("src/test/resources/wrongprofilekindsampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(wrongProfileKindFileObject, "eclipse-demo");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No valid <profile> tag found in " + wrongProfileKindFileObject.getPath(), ex.getMessage());
		}

		FileObject defectFormatterFileObject = ConfigReader.toFileObject("src/test/resources/defectformattersampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(defectFormatterFileObject, "eclipse-demo");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profiles> tag found in config file", ex.getMessage());
		}

		FileObject noProfileFileObject = ConfigReader.toFileObject("src/test/resources/noprofilesampleeclipse.xml");

		try {
			ConfigReader.getProfileSettings(noProfileFileObject, "eclipse-demo");

			Assert.assertTrue("Exception was expected", false);
		} catch (Exception ex) {
			Assert.assertEquals("No <profile> tag found in " + noProfileFileObject.getPath(), ex.getMessage());
		}

		Map<String, String> profileSettings = ConfigReader.getProfileSettings(fileObject, "eclipse-demo");

		Assert.assertFalse(profileSettings.isEmpty());
		Assert.assertEquals(1, profileSettings.size());
		Assert.assertEquals("70", profileSettings.get("org.eclipse.jdt.core.formatter.alignment_for_enum_constants"));
	}
}