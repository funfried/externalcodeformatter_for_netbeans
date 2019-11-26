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
package de.funfried.netbeans.plugins.external.formatter;

import javax.swing.Icon;
import javax.swing.text.Document;

import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.ImageUtilities;

public class Utils {
	@StaticResource
	public static final String EXTERNAL_FORMATTER_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/external.gif";

	@StaticResource
	public static final String ECLIPSE_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/eclipse.gif";

	@StaticResource
	public static final String GOOGLE_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/google.gif";

	@StaticResource
	public static final String NETBEANS_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/netbeans.gif";

	public static Icon iconExternal = ImageUtilities.image2Icon(ImageUtilities.loadImage(EXTERNAL_FORMATTER_ICON_PATH));

	public static Icon iconEclipse = ImageUtilities.image2Icon(ImageUtilities.loadImage(ECLIPSE_ICON_PATH));

	public static Icon iconGoogle = ImageUtilities.image2Icon(ImageUtilities.loadImage(GOOGLE_ICON_PATH));

	public static Icon iconNetBeans = ImageUtilities.image2Icon(ImageUtilities.loadImage(NETBEANS_ICON_PATH));

	public static boolean isJava(Document document) {
		return "text/x-java".equals(NbEditorUtilities.getMimeType(document));
	}
}
