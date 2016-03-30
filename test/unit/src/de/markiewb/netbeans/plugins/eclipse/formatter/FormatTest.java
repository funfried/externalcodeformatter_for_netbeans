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
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\formattersampleeclipse.xml", "eclipse-demo", null, null, TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest {A,B,C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = format(formatter, text);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    @Test
    public void testFormatUsingEPF() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\mechanic-formatter.epf", null, null, null, TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = format(formatter, text);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    @Test
    public void testFormatUsingProjectSettings() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, null, null, TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = format(formatter, text);
        assertEquals("Formatting should change the code", expected, actual.replaceAll("\r", ""));
    }

    @Test
    public void testFormatUsingLinefeed_CR() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\r", null, TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\r"
                + "\r"
                + "public enum NewEmptyJUnitTest {\r"
                + "			       A,\r"
                + "				   B,\r"
                + "				   C}";
        String actual = format(formatter, text);
        assertEquals("Formatting should change the code", expected, actual);
    }

    @Test
    public void testFormatUsingLinefeed_LF() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", null, TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = format(formatter, text);
        assertEquals("Formatting should change the code", expected, actual);
    }

    @Test
    public void testFormatUsingLinefeed_CRLF() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\r\\n", null, TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\r\n"
                + "\r\n"
                + "public enum NewEmptyJUnitTest {\r\n"
                + "			       A,\r\n"
                + "				   B,\r\n"
                + "				   C}";
        String actual = format(formatter, text);
        assertEquals("Formatting should change the code", expected, actual);
    }

    @Test
    public void testFormatSourceLevel_1dot3() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", "1.3", TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = null;
        String actual = format(formatter, text);
        assertEquals("Invalid source code for 1.3 - enum is not a keyword", expected, actual);
    }

    @Test
    public void testFormatSourceLevel_1dot4() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", "1.4", TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = null;
        String actual = format(formatter, text);
        assertEquals("Invalid source code for 1.4 - enum is not a keyword", expected, actual);
    }

    @Test
    public void testFormatSourceLevel_1dot5() {
        EclipseFormatter formatter = new EclipseFormatter("C:\\Users\\markiewb\\Desktop\\NetBeansProjects\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\\n", "1.5", TestableBreakpointsProviderImpl.EMTPY());
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\n"
                + "\n"
                + "public enum NewEmptyJUnitTest {\n"
                + "			       A,\n"
                + "				   B,\n"
                + "				   C}";
        String actual = format(formatter, text);
        assertEquals("Invalid source code for 1.4 - enum is not a keyword", expected, actual);
    }

    private static String format(EclipseFormatter formatter, final String text) {
        return formatter.forCode(text, 0, text.length() - 1, null);
    }
}
