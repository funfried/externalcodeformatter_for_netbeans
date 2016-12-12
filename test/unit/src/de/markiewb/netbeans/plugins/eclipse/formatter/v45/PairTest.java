/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.markiewb.netbeans.plugins.eclipse.formatter.v45;

import de.markiewb.netbeans.plugins.eclipse.formatter.v45.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author markiewb
 */
public class PairTest {
    
    @Test
    public void testCompareToFirst() {
        Pair a = Pair.of(1, 10);
        Pair b = Pair.of(2, 10);
        Pair c = Pair.of(3, 10);
        final TreeSet<Pair> set = new TreeSet<Pair>();
        set.add(a);
        set.add(b);
        set.add(c);
        List<Pair> act = new ArrayList<>(set);
        
        assertEquals(a, act.get(0));
        assertEquals(b, act.get(1));
        assertEquals(c, act.get(2));
    }
    @Test
    public void testCompareToSame() {
        Pair a = Pair.of(1, 10);
        final TreeSet<Pair> set = new TreeSet<Pair>();
        set.add(a);
        set.add(a);
        set.add(a);
        List<Pair> act = new ArrayList<>(set);
        
        assertEquals(a, act.get(0));
        assertEquals(1, act.size());
    }
    @Test
    public void testCompareToSecond() {
        Pair a = Pair.of(0, 1);
        Pair b = Pair.of(0, 2);
        Pair c = Pair.of(0, 3);
        final TreeSet<Pair> set = new TreeSet<Pair>();
        set.add(a);
        set.add(b);
        set.add(c);
        List<Pair> act = new ArrayList<>(set);
        
        assertEquals(a, act.get(0));
        assertEquals(b, act.get(1));
        assertEquals(c, act.get(2));
    }

    @Test
    public void testGetFirst() {
    }

    @Test
    public void testGetSecond() {
    }
    
}
