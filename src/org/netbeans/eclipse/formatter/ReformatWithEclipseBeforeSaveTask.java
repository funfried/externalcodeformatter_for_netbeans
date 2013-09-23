package org.netbeans.eclipse.formatter;

import java.util.concurrent.atomic.AtomicBoolean;
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
