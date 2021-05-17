/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author bahlef
 */
public class JSQLFormatterWrapperTest {
	private static JSQLFormatterWrapper instance;

	@BeforeClass
	public static void setUpClass() {
		instance = new JSQLFormatterWrapper();
	}

	@Test
	public void testFormat() {
		final String text = "SELECT FOO FROM BAR WHERE FOO = 'BAR' ORDER BY FOO LIMIT 1";
		final String expected = "SELECT foo\n" +
				"FROM bar\n" +
				"WHERE foo = 'BAR'\n" +
				"ORDER BY foo\n" +
				";";

		String actual = instance.format(text);
		Assert.assertNotNull("Formatting should not return null value", actual);
		Assert.assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
	}

	@Test
	public void testNullCode() {
		String actual = instance.format(null);
		Assert.assertNull("Formatting shouldn't change the code, should still be null", actual);
	}
}
