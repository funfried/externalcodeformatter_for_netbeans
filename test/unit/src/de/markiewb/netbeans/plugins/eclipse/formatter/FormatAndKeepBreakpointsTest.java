package de.markiewb.netbeans.plugins.eclipse.formatter;

import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author markiewb
 */
public class FormatAndKeepBreakpointsTest {

    @Test
    public void testFormatWholeFile() throws URISyntaxException {
        File f = new File(this.getClass().getClassLoader().getResource("formattersampleeclipse.xml").toURI());
        EclipseFormatter formatter = new EclipseFormatter(f.getAbsolutePath(), "eclipse-demo", null, null);
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
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

}
