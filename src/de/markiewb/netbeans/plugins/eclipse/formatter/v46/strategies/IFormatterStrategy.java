/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 */
package de.markiewb.netbeans.plugins.eclipse.formatter.v46.strategies;

import de.markiewb.netbeans.plugins.eclipse.formatter.v46.strategies.eclipse.EclipseFormatter;

/**
 *
 * @author markiewb
 */
public interface IFormatterStrategy {

    void format(final EclipseFormatter formatter, final boolean preserveBreakpoints, ParameterObject po);

}
