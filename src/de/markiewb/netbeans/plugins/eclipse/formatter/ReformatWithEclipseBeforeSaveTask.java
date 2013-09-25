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

import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ECLIPSE_FORMATTER_ENABLED;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.SHOW_NOTIFICATIONS;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.getActivePreferences;
import de.markiewb.netbeans.plugins.eclipse.formatter.options.EclipseFormatterPanel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.document.OnSaveTask;

public class ReformatWithEclipseBeforeSaveTask implements OnSaveTask {

    private final Document document;
    private AtomicBoolean canceled = new AtomicBoolean();

    ReformatWithEclipseBeforeSaveTask(Document doc) {
        this.document = doc;
        
    }

    @Override
    public void performTask() {

        
        
        
        final StyledDocument styledDoc = (StyledDocument) document;

        
        Preferences pref = getActivePreferences(styledDoc);

        final boolean isEclipseFormatterEnabled = pref.getBoolean(ECLIPSE_FORMATTER_ENABLED, false);
        final boolean showNotifications = pref.getBoolean(SHOW_NOTIFICATIONS, false);
        
        new FormatJavaAction().format(styledDoc);
        
        
        
    }

    @Override
    public void runLocked(Runnable run) {
        run.run();
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return true;
    }

    @MimeRegistration(mimeType = "text/x-java", service = OnSaveTask.Factory.class, position = 1500)
    public static final class FactoryImpl implements Factory {

        @Override
        public OnSaveTask createTask(Context context) {
            return new ReformatWithEclipseBeforeSaveTask(context.getDocument());
        }
    }

}
