package de.markiewb.netbeans.plugins.eclipse.formatter;

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
        final String text = "package foo;public class NewEmptyJUnitTest {}";
        String actual = formatter.forCode(text, 0, text.length() - 1);
        assertFalse("Formatting should change the code", actual.equals(text));
    }

    @Test
    public void testFormatUsingEPF() {
        EclipseFormatter formatter = new EclipseFormatter("D:\\ws\\eclipsecodeformatter_for_netbeans\\test\\unit\\src\\mechanic-formatter.epf", null);
        final String text = "package foo;public class NewEmptyJUnitTest {}";
        String actual = formatter.forCode(text, 0, text.length() - 1);
        assertFalse("Formatting should change the code", actual.equals(text));
    }

}
