/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.sqlformatter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import com.github.vertical_blank.sqlformatter.languages.Dialect;

/**
 *
 * @author bahlef
 */
public class SQLFormatterWrapperTest {
	private static SQLFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new SQLFormatterWrapper();
	}

	@Test
	public void testFormat() {
		final String text = "SELECT FOO FROM BAR WHERE FOO = 'BAR' ORDER BY FOO LIMIT 1";
		final String expected = "SELECT\n" +
				"  FOO\n" +
				"FROM\n" +
				"  BAR\n" +
				"WHERE\n" +
				"  FOO = 'BAR'\n" +
				"ORDER BY\n" +
				"  FOO\n" +
				"LIMIT\n" +
				"  1";

		FormatConfig formatConfig = FormatConfig.builder().indent("  ").maxColumnLength(120).uppercase(true).build();
		String actual = instance.format(text, Dialect.PostgreSql, formatConfig);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testNullCode() {
		FormatConfig formatConfig = FormatConfig.builder().indent("  ").maxColumnLength(120).uppercase(true).build();
		String actual = instance.format(null, Dialect.PostgreSql, formatConfig);
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}
}
