package de.markiewb.netbeans.plugins.eclipse.formatter;

import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter;
import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatterWithSections;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import org.netbeans.api.debugger.jpda.LineBreakpoint;

/**
 *
 * @author markiewb
 */
public class FormatAndKeepBreakpointsTest {

    @Test
    public void testFormatWholeFile() throws Exception {
        final String text = "package foo;\n"
                + "\n"
                + "public class NewClass1 {\n"
                + "    String var = \"1234\";\n"
                + "\n"
                + "    public     void    sayOut() {\n"
                + "        System.out.println();\n"
                + "    }\n"
                + "    public static    void     sayErr() {\n"
                + "        System.err.println();\n"
                + "    }\n"
                + "}\n";
        final String expected = "package foo;\n"
                + "\n"
                + "public class NewClass1 {\n"
                + "    String var = \"1234\";\n"
                + "\n"
                + "    public void sayOut() {\n"
                + "	System.out.println();\n"
                + "    }\n"
                + "\n"
                + "    public static void sayErr() {\n"
                + "	System.err.println();\n"
                + "    }\n"
                + "}\n"
                + "";
        String actual = format(text);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    @Test
    public void testFormatWholeFileOneLineBreakpoint() throws Exception {
        final String text = "package foo;\n"
                + "\n"
                + "public class NewClass1 {\n"
                + "    String var = \"1234\";\n"
                + "\n"
                + "LBP    public     void    sayOut() {\n"
                + "        System.out.println();\n"
                + "    }\n"
                + "    public static    void     sayErr() {\n"
                + "        System.err.println();\n"
                + "    }\n"
                + "}\n";
        final String expected = "package foo;\n"
                + "\n"
                + "public class NewClass1 {\n"
                + "    String var = \"1234\";\n"
                + "\n"
                + "    public void sayOut() {\n"
                + "	System.out.println();\n"
                + "    }\n"
                + "\n"
                + "    public static void sayErr() {\n"
                + "	System.err.println();\n"
                + "    }\n"
                + "}\n";
        String actual = format(text);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    private String format(final String input) throws Exception {

        List<Integer> lbkps = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        List<String> lines = Arrays.asList(input.split("\n"));
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("LBP")) {
                lbkps.add(i);
                sb.append(line.substring("LBP".length()));
            } else {
                sb.append(line);
            }
            sb.append("\n");
        }
        String text = sb.toString();

        File f = new File(this.getClass().getClassLoader().getResource("formattersampleeclipse.xml").toURI());
        EclipseFormatter formatter = new EclipseFormatter(f.getAbsolutePath(), "eclipse-demo", null, null, new TestableBreakpointsProviderImpl(Collections.<LineBreakpoint>emptyList()));

        EclipseFormatterWithSections instance = new EclipseFormatterWithSections();

        StyledDocument document = new DefaultStyledDocument();
        document.insertString(0, text, null);
        String actual = instance.formatSections(formatter, document, lbkps, text);
        return actual.replaceAll("\r", "");
    }
}
