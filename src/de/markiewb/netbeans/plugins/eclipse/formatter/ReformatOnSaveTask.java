/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 *    Saad Mufti <saad.mufti@teamaol.com> 
 */
package de.markiewb.netbeans.plugins.eclipse.formatter;

import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ENABLE_SAVEACTION;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.getActivePreferences;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.document.OnSaveTask;

public class ReformatOnSaveTask implements OnSaveTask {

    private final Context context;

    private ReformatOnSaveTask(Context context) {
        this.context = context;
    }

    @Override
    public void performTask() {
        final StyledDocument styledDoc = (StyledDocument) this.context.getDocument();
        Preferences pref = getActivePreferences(styledDoc);

        final boolean enableSaveAction = pref.getBoolean(ENABLE_SAVEACTION, false);
        if (enableSaveAction) {
            final boolean isSaveAction = true;
            new FormatJavaAction().format(styledDoc, isSaveAction);
        }
    }

    @Override
    public void runLocked(Runnable run) {
        run.run();
    }

    @Override
    public boolean cancel() {
        return true;
    }

    @MimeRegistration(mimeType = "text/x-java", service = OnSaveTask.Factory.class, position = 1500)
    public static final class FactoryImpl implements Factory {

        @Override
        public OnSaveTask createTask(Context context) {
            return new ReformatOnSaveTask(context);
        }
    }

}
