/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 * @author markiewb
 */
public class Sectionizer {

    public List<Section> sectionise(List<Integer> lineNumbersWithBreakpoint, int maxLine) {
        List<Section> result = new ArrayList<>();
        // every breakpoint line must be [minLine..maxLine]

        List<Integer> sorted = new ArrayList<>(lineNumbersWithBreakpoint);
        sorted.add(-1);
        sorted.add(maxLine + 1);
        Collections.sort(sorted);
        if (0 == maxLine && lineNumbersWithBreakpoint.isEmpty()) {
            return Collections.singletonList(new Section(0, 0));
        }

        //head
        // 0..4
        {
            final Integer startL = sorted.get(0) + 1;
            final Integer endL = sorted.get(1) - 1;
            if (startL < endL) {

                result.add(new Section(startL, endL));
            }
        }
        //middle
        {
            for (int i = 1; i < sorted.size() - 1; i++) {
                //5..5
                result.add(new Section(sorted.get(i + 0), sorted.get(i + 0)));
            }
        }
        //tail
        {
            for (int i = sorted.size() - 1; i < sorted.size(); i++) {
                final int startL = sorted.get(i - 1) + 1;
                final int endL = sorted.get(i + 0) - 1;
                // 6..10
                if (startL < endL) {

                    result.add(new Section(startL, endL));
                }
            }
        }

        //remove dupliates
        return new ArrayList<>(new LinkedHashSet<>(result));
    }

    public static class Section {

        int startLineIncluding;
        int endLineIncluding;

        public int getStartLine() {
            return startLineIncluding;
        }

        public int getEndLine() {
            return endLineIncluding;
        }

        public Section(int startLineIncluding, int endLineIncluding) {
            this.startLineIncluding = startLineIncluding;
            this.endLineIncluding = endLineIncluding;
        }

        @Override
        public String toString() {
            return "{" + startLineIncluding + ".." + endLineIncluding + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.startLineIncluding;
            hash = 79 * hash + this.endLineIncluding;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Section other = (Section) obj;
            if (this.startLineIncluding != other.startLineIncluding) {
                return false;
            }
            if (this.endLineIncluding != other.endLineIncluding) {
                return false;
            }
            return true;
        }

    }

}
