/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.dbeaver;

import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author bahlef
 */
public class DBeaverFormatterWrapperTest {
	private static DBeaverFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new DBeaverFormatterWrapper();
	}

	@Test
	public void testFormat() {
		final String text = "SELECT FOO FROM BAR WHERE FOO = 'BAR' ORDER BY FOO LIMIT 1";
		final String expected = "SELECT\n" +
				"    FOO\n" +
				"FROM\n" +
				"    BAR\n" +
				"WHERE\n" +
				"    FOO = 'BAR'\n" +
				"ORDER BY\n" +
				"    FOO LIMIT 1";

		Properties props = new Properties();
		props.put(DBeaverFormatterSettings.INDENT_SIZE, DBeaverFormatterSettings.INDENT_SIZE_DEFAULT);
		props.put(DBeaverFormatterSettings.INDENT_TYPE, DBeaverFormatterSettings.INDENT_TYPE_DEFAULT);
		props.put(DBeaverFormatterSettings.KEYWORD_CASE, DBeaverFormatterSettings.KEYWORD_CASE_DEFAULT);
		props.put(DBeaverFormatterSettings.STATEMENT_DELIMITER, DBeaverFormatterSettings.STATEMENT_DELIMITER_DEFAULT);

		String actual = instance.format(text, props);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testNullCode() {
		Properties props = new Properties();
		props.put(DBeaverFormatterSettings.INDENT_SIZE, DBeaverFormatterSettings.INDENT_SIZE_DEFAULT);
		props.put(DBeaverFormatterSettings.INDENT_TYPE, DBeaverFormatterSettings.INDENT_TYPE_DEFAULT);
		props.put(DBeaverFormatterSettings.KEYWORD_CASE, DBeaverFormatterSettings.KEYWORD_CASE_DEFAULT);
		props.put(DBeaverFormatterSettings.STATEMENT_DELIMITER, DBeaverFormatterSettings.STATEMENT_DELIMITER_DEFAULT);

		String actual = instance.format(null, props);
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}
}
