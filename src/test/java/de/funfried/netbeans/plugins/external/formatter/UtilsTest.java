/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author bahlef
 */
public class UtilsTest {
	public UtilsTest() {
	}

	@Test
	public void testisJavaWithNullDocument() throws Exception {
		Assert.assertFalse("Null document should not be a Java document", Utils.isJava(null));
	}
}
