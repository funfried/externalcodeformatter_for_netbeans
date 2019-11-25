/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.ECLIPSE.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * Saad Mufti <saad.mufti@teamaol.com>
 */
package de.funfried.netbeans.plugins.eclipse.formatter;

import javax.swing.Icon;
import javax.swing.text.Document;

import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.ImageUtilities;

public class Utils {
	@StaticResource
	private static final String ECLIPSE = "de/funfried/netbeans/plugins/eclipse/formatter/eclipse.gif";

	@StaticResource
	private static final String NETBEANS = "de/funfried/netbeans/plugins/eclipse/formatter/netbeans.gif";

	public static Icon iconEclipse = ImageUtilities.image2Icon(ImageUtilities.loadImage(ECLIPSE));

	public static Icon iconNetBeans = ImageUtilities.image2Icon(ImageUtilities.loadImage(NETBEANS));

	public static boolean isJava(Document document) {
		return "text/x-java".equals(NbEditorUtilities.getMimeType(document));
	}
}
