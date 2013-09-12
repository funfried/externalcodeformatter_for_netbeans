package org.netbeans.eclipse.formatter;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.awt.NotificationDisplayer;

public class ReformatWithEclipseBeforeSaveTask implements OnSaveTask {

    private final Document document;
    private EclipseFormatter formatter;
    private AtomicBoolean canceled = new AtomicBoolean();

    ReformatWithEclipseBeforeSaveTask(Document doc) {
        this.document = doc;
        this.formatter = EclipseFormatterUtilities.getEclipseFormatter();
    }
    
    @Override
    public void performTask() {
        final StyledDocument styledDoc = (StyledDocument) document;
        GuardedSectionManager guards = GuardedSectionManager.getInstance(styledDoc);
        EclipseFormatterUtilities u = new EclipseFormatterUtilities();
        if (guards == null && this.formatter != null) {
            u.reFormatWithEclipse(styledDoc, formatter, u.isJava(styledDoc));
        } else {
            if (EclipseFormatterUtilities.getGlobalPrefs().getBoolean("globalEclipseFormatterDebug", false) == true) {
                NotificationDisplayer.getDefault().notify("NetBeans formatter", EclipseFormatterUtilities.icon, "(Files with guarded blocks are not supported by Eclipse formatters)", null);
            }
            u.reformatWithNetBeans(styledDoc);
        }
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
