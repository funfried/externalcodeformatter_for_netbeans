/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.ECLIPSE.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 *    Saad Mufti <saad.mufti@teamaol.com> 
 */
package de.markiewb.netbeans.plugins.eclipse.formatter;

import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter;
import javax.swing.Icon;
import javax.swing.text.Document;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.ImageUtilities;

public class Utilities {

    @StaticResource
    private static final String ECLIPSE = "de/markiewb/netbeans/plugins/eclipse/formatter/eclipse.gif";
    @StaticResource
    private static final String NETBEANS = "de/markiewb/netbeans/plugins/eclipse/formatter/netbeans.gif";

    public static Icon iconEclipse = ImageUtilities.image2Icon(ImageUtilities.loadImage(ECLIPSE));
    public static Icon iconNetBeans = ImageUtilities.image2Icon(ImageUtilities.loadImage(NETBEANS));

    public static boolean isJava(Document document) {
        return "text/x-java".equals(NbEditorUtilities.getMimeType(document));
    }

}
