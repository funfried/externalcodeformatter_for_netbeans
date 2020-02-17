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
	static final String EXTERNAL_FORMATTER_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/external.gif";

	/**
	 * Path to the Eclipse icon.
	 */
	@StaticResource
	static final String ECLIPSE_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/eclipse.gif";

	/**
	 * Path to the Google icon.
	 */
	@StaticResource
	static final String GOOGLE_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/google.gif";

	/**
	 * Path to the Spring icon.
	 */
	@StaticResource
	static final String SPRING_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/spring.gif";

	/**
	 * Path to the NetBeans icon.
	 */
	@StaticResource
	static final String NETBEANS_ICON_PATH = "de/funfried/netbeans/plugins/external/formatter/netbeans.gif";

	/**
	 * External icon.
	 */
	static final Icon ICON_EXTERNAL = ImageUtilities.image2Icon(ImageUtilities.loadImage(EXTERNAL_FORMATTER_ICON_PATH));

	/**
	 * Eclipse icon.
	 */
	static final Icon ICON_ECLIPSE = ImageUtilities.image2Icon(ImageUtilities.loadImage(ECLIPSE_ICON_PATH));

	/**
	 * Google icon.
	 */
	static final Icon ICON_GOOGLE = ImageUtilities.image2Icon(ImageUtilities.loadImage(GOOGLE_ICON_PATH));

	/**
	 * Spring icon.
	 */
	static final Icon ICON_SPRING = ImageUtilities.image2Icon(ImageUtilities.loadImage(SPRING_ICON_PATH));

	/**
	 * NetBeans icon.
	 */
	static final Icon ICON_NETBEANS = ImageUtilities.image2Icon(ImageUtilities.loadImage(NETBEANS_ICON_PATH));
}
