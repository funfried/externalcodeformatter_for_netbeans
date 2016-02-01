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
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\formattersampleeclipse.xml", "eclipse-demo");
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
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\mechanic-formatter.epf", null);
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
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\org.eclipse.jdt.core.prefs", null);
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

}
