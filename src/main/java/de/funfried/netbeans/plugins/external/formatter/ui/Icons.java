/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui;

import javax.swing.Icon;

import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;

/**
 * Interface for holding application icons.
 *
 * @author bahlef
 */
public interface Icons {
	/**
	 * Path to the external icon.
	 */
	@StaticResource
	String EXTERNAL_FORMATTER_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/external.gif";

	/**
	 * Path to the Eclipse icon.
	 */
	@StaticResource
	String ECLIPSE_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/eclipse.gif";

	/**
	 * Path to the Google icon.
	 */
	@StaticResource
	String GOOGLE_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/google.gif";

	/**
	 * Path to the Spring icon.
	 */
	@StaticResource
	String SPRING_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/spring.gif";

	/**
	 * Path to the FasterXML Jackson icon.
	 */
	@StaticResource
	String JACKSON_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/jackson.gif";

	/**
	 * Path to the revelc.net icon.
	 */
	@StaticResource
	String REVELC_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/revelc.gif";

	/**
	 * Path to the Jsoup icon.
	 */
	@StaticResource
	String JSOUP_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/jsoup.gif";

	/**
	 * Path to the NetBeans icon.
	 */
	@StaticResource
	String NETBEANS_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/netbeans.gif";

	/**
	 * Path to the manticore-projects icon.
	 */
	@StaticResource
	String MANTICORE_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/manticore.png";

	/**
	 * External icon.
	 */
	Icon ICON_EXTERNAL = ImageUtilities.image2Icon(ImageUtilities.loadImage(EXTERNAL_FORMATTER_ICON_PATH));

	/**
	 * Eclipse icon.
	 */
	Icon ICON_ECLIPSE = ImageUtilities.image2Icon(ImageUtilities.loadImage(ECLIPSE_ICON_PATH));

	/**
	 * Google icon.
	 */
	Icon ICON_GOOGLE = ImageUtilities.image2Icon(ImageUtilities.loadImage(GOOGLE_ICON_PATH));

	/**
	 * Spring icon.
	 */
	Icon ICON_SPRING = ImageUtilities.image2Icon(ImageUtilities.loadImage(SPRING_ICON_PATH));

	/**
	 * Jackson icon.
	 */
	Icon ICON_JACKSON = ImageUtilities.image2Icon(ImageUtilities.loadImage(JACKSON_ICON_PATH));

	/**
	 * revelc.net icon.
	 */
	Icon ICON_REVELC = ImageUtilities.image2Icon(ImageUtilities.loadImage(REVELC_ICON_PATH));

	/**
	 * Jsoup icon.
	 */
	Icon ICON_JSOUP = ImageUtilities.image2Icon(ImageUtilities.loadImage(JSOUP_ICON_PATH));

	/**
	 * NetBeans icon.
	 */
	Icon ICON_NETBEANS = ImageUtilities.image2Icon(ImageUtilities.loadImage(NETBEANS_ICON_PATH));

	/**
	 * manticore-projects icon.
	 */
	Icon ICON_MANTICORE = ImageUtilities.image2Icon(ImageUtilities.loadImage(MANTICORE_ICON_PATH));
}
