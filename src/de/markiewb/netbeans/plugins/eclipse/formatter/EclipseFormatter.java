/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 */
package de.markiewb.netbeans.plugins.eclipse.formatter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReadException;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReader;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.xml.sax.SAXException;

public final class EclipseFormatter {

    private final String formatterFile;

    EclipseFormatter(String formatterFile) {
        this.formatterFile = formatterFile;
    }

//     NotificationDisplayer.getDefault().notify("Using the Global Eclipse formatter", icon, message, null);
    private Map getFormattingOptions() {
        Map options = DefaultCodeFormatterConstants.getJavaConventionsSettings();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
//      For checking whether the Eclipse formatter works,
//      without needing an Eclipse formatter XML file:
//        options.put(
//		DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
//		DefaultCodeFormatterConstants.createAlignmentValue(
//		true,
//		DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
//		DefaultCodeFormatterConstants.INDENT_ON_COLUMN));

        return options;
    }

    private String format(final String code) throws MalformedTreeException, BadLocationException {
        final int opts = CodeFormatter.K_COMPILATION_UNIT + CodeFormatter.F_INCLUDE_COMMENTS;
        Map allConfig = new HashMap();
        final Map configFromStatic = getFormattingOptions();
        try {
            Map configFromFile = new ConfigReader().read(FileUtil.normalizeFile(new File(formatterFile)));

            allConfig.putAll(configFromStatic);
            allConfig.putAll(configFromFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SAXException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ConfigReadException ex) {
            Exceptions.printStackTrace(ex);
        }
        CodeFormatter formatter = ToolFactory.createCodeFormatter(allConfig);
        final TextEdit te = formatter.format(opts, code, 0, code.length(), 0, null);
        final IDocument dc = new Document(code);
        String formattedCode = code;
        if (te != null) {
            te.apply(dc);
            formattedCode = dc.get();
        }
        return formattedCode.toString();
    }

    public String forCode(final String code) {
        String result = null;
        try {
            if (code != null) {
                result = this.format(code);
            }
        } catch (Exception ex) {
            System.out.println(ex);
            Logger.getLogger(EclipseFormatter.class.getName()).log(Level.SEVERE,
                    "code could not be formatted!", ex);
            System.out.println(ex);
        }
        return result;
    }

}
