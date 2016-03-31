/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author markiewb
 */
public class SectionizerTest {

    @Test
    public void testSectionise_NoSplit_Section0to10() {

        List<Integer> lineNumbersWithBreakpoint = Arrays.asList();
        int maxLine = 10;
        Sectionizer instance = new Sectionizer();
        List<Sectionizer.Section> expResult = Arrays.asList(new Sectionizer.Section(0, 10));
        List<Sectionizer.Section> result = instance.sectionise(lineNumbersWithBreakpoint, maxLine);
        assertEquals(expResult, result);
    }

    @Test
    public void testSectionise_SplitAtHead_TwoSections() {

        List<Integer> lineNumbersWithBreakpoint = Arrays.asList(0);
        int maxLine = 10;
        Sectionizer instance = new Sectionizer();
        List<Sectionizer.Section> expResult = Arrays.asList(new Sectionizer.Section(0, 0), new Sectionizer.Section(1, 10));
        List<Sectionizer.Section> result = instance.sectionise(lineNumbersWithBreakpoint, maxLine);
        assertEquals(expResult, result);
    }

    @Test
    public void testSectionise_SplitAtTail_TwoSections() {

        List<Integer> lineNumbersWithBreakpoint = Arrays.asList(10);
        int maxLine = 10;
        Sectionizer instance = new Sectionizer();
        List<Sectionizer.Section> expResult = Arrays.asList(new Sectionizer.Section(0, 9), new Sectionizer.Section(10, 10));
        List<Sectionizer.Section> result = instance.sectionise(lineNumbersWithBreakpoint, maxLine);
        assertEquals(expResult, result);
    }

    @Test
    public void testSectionise_SplitAt5_ThreeSections() {

        List<Integer> lineNumbersWithBreakpoint = Arrays.asList(5);
        int maxLine = 10;
        Sectionizer instance = new Sectionizer();
        List<Sectionizer.Section> expResult = Arrays.asList(new Sectionizer.Section(0, 4), new Sectionizer.Section(5, 5), new Sectionizer.Section(6, 10));
        List<Sectionizer.Section> result = instance.sectionise(lineNumbersWithBreakpoint, maxLine);
        assertEquals(expResult, result);
    }

    @Test
    public void testSectionise_OneLine_Section0to0() {

        List<Integer> lineNumbersWithBreakpoint = Arrays.asList();
        int maxLine = 0;
        Sectionizer instance = new Sectionizer();
        List<Sectionizer.Section> expResult = Arrays.asList(new Sectionizer.Section(0, 0));
        List<Sectionizer.Section> result = instance.sectionise(lineNumbersWithBreakpoint, maxLine);
        assertEquals(expResult, result);
    }

    @Test
    public void testSectionise_OneLineWithBreakpointAt0_Section0to0() {

        List<Integer> lineNumbersWithBreakpoint = Arrays.asList(0);
        int maxLine = 0;
        Sectionizer instance = new Sectionizer();
        List<Sectionizer.Section> expResult = Arrays.asList(new Sectionizer.Section(0, 0));
        List<Sectionizer.Section> result = instance.sectionise(lineNumbersWithBreakpoint, maxLine);
        assertEquals(expResult, result);
    }

    @Test
    public void testSectionise_NoLine_NoSection() {

        List<Integer> lineNumbersWithBreakpoint = Arrays.asList();
        int maxLine = -1;
        Sectionizer instance = new Sectionizer();
        List<Sectionizer.Section> expResult = Arrays.asList();
        List<Sectionizer.Section> result = instance.sectionise(lineNumbersWithBreakpoint, maxLine);
        assertEquals(expResult, result);
    }

}
