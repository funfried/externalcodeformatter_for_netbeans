package de.markiewb.netbeans.plugins.eclipse.formatter;

import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.EclipseFormatter;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import org.junit.Test;

/**
 *
 * @author markiewb
 */
public class FormatTest {

    @Test
    public void testFormatUsingXML() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\formattersampleeclipse.xml", "eclipse-demo", null);
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
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\mechanic-formatter.epf", null, null);
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
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, null);
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
    public void testFormatUsingLinefeed_CR() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\r");
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
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\n");
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
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\r\n");
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
    public void testFormatUsingLinefeed_Custom() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null, "\t");
        final String text = "package foo;public enum NewEmptyJUnitTest { A, B, C}";
        final String expected = "package foo;\t"
                + "\t"
                + "public enum NewEmptyJUnitTest {\t"
                + "			       A,\t"
                + "				   B,\t"
                + "				   C}";
        String actual = formatter.forCode(text, 0, text.length() - 1, null);
        assertEquals("Formatting should change the code", expected, actual);
    }

}
