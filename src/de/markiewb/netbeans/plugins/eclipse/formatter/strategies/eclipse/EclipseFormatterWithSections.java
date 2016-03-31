package de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse;

import de.markiewb.netbeans.plugins.eclipse.formatter.Pair;
import java.util.Collections;
import java.util.List;
import javax.swing.text.StyledDocument;
import org.openide.text.NbDocument;

/**
 *
 * @author markiewb
 */
public class EclipseFormatterWithSections {

    public String formatSections(EclipseFormatter formatter, StyledDocument document, List<Integer> linebreakPointsLines, final String docText) {
        StringBuilder c = new StringBuilder();
        //FIXME better algorithm
        String text = docText;
        int maxLine = text.replaceAll("\r\n", "\n").replaceAll("\r", "\n").split("\n").length - 1;
        List<Sectionizer.Section> sections = new Sectionizer().sectionise(linebreakPointsLines, maxLine);
        for (Sectionizer.Section section : sections) {
            int start = getOffsetForLine(document, section.startLineIncluding);
            int end = getOffsetForLine(document, section.endLineIncluding + 1);
            String formattedContent = formatter.forCode(docText, start, end, Collections.<Pair>emptySortedSet());
            StringBuilder s = new StringBuilder(formattedContent);

            // remove lines from tail
            s.delete(formattedContent.length() - (docText.length() - end), formattedContent.length());
            // remove lines from head
            s.delete(0, start);

            c.append(s.toString());
        }
        /**
         * <pre>
         * 0
         * 1
         * 2 BK
         * 3
         * 4
         *
         * sections SEC:
         * 0..1
         * 2..2
         * 3..4
         *
         *  . Collector c &lt;= empty
         *  . s &lt;= split sections by linebreakpoints (one single line section for one linebreakpoint)
         *  . Foreach i from s
         *  . .     d &lt;= format whole document using s[i].startLineOffset..s[i].endLineOffset
         *  . .     p &lt;= extract part from d, which has changed
         *  . . .     p &lt;= d
         *  . . .     p &lt;= remove s[i+1].startLine..s[max].endLine from p // remove from tail
         *  . . .     p &lt;= remove s[min].startLine..s[i-1].endLine from p // remove from head
         *  . . .     return p
         *  . .     c &lt;= add p to c
         *  . .     lineMap &lt;= Remember, which line is mapped to lines (sections could be expanded to several lines)
         *  . Replace text with c
         *  . Foreach oldLineIndex from linebreaks
         *  . . newLines &lt;=lineMap[oldlineIndex]
         *  . . try to set Breakpoints at each newLine
         *
         *
         * </pre>
         */
        return c.toString();
    }

    private int getOffsetForLine(StyledDocument document1, int line) {
        try {
            return NbDocument.findLineOffset(document1, line);
        } catch (Exception e) {
            return document1.getLength();
        }
    }
}
