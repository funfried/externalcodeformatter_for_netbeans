package de.markiewb.netbeans.plugins.eclipse.formatter;

import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author markiewb
 */
public class FormatTest {

    @Test
    public void testFormatUsingXML() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\formattersampleeclipse.xml", "eclipse-demo", null, null);
        final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    @Test
    public void testFormatUsingEPF() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\mechanic-formatter.epf", null, null, null);
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    @Test
    public void testFormatUsingProjectSettings() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, null, null);
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    /**
     * https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77
     */
    @Test
    public void testFormatUsingProjectSettings_ExplicitDefaultFormatter() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\defaultformatter_org.eclipse.jdt.core.prefs", null, null, null);
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    /**
     * https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77
     */
    @Test
    public void testFormatUsingProjectSettings_Explicit3rdPartyFormatter_Failure() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\3rdPartyFormatter_org.eclipse.jdt.core.prefs", null, null, null);
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        try {
            formatter.forCode(text, 0, text.length() - 1, null);
        } catch (Exception e) {
            assertEquals(true, e.getMessage().contains("The use of third-party Java code formatters is not supported by this plugin."));
        }
    }

    @Test
    public void testFormatUsingLinefeed_CR() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\r", null);
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\r"
                + "\r"
                + "public enum NewEmptyJUnitTest {\r"
                + "			       A,\r"
                + "				   B,\r"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual);
    }

    @Test
    public void testFormatUsingLinefeed_LF() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", null);
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual);
    }

    @Test
    public void testFormatUsingLinefeed_CRLF() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\r\\n", null);
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\r\n"
                + "\r\n"
                + "public enum NewEmptyJUnitTest {\r\n"
                + "			       A,\r\n"
                + "				   B,\r\n"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual);
    }

    @Test
    public void testFormatSourceLevel_1dot3() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", "1.3");
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = null;
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Invalid source code for 1.3 - enum is not a keyword", expected, actual);
    }

    @Test
    public void testFormatSourceLevel_1dot4() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", "1.4");
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = null;
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Invalid source code for 1.4 - enum is not a keyword", expected, actual);
    }

    @Test
    public void testFormatSourceLevel_1dot5() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", "1.5");
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Invalid source code for 1.4 - enum is not a keyword", expected, actual);
    }
}
