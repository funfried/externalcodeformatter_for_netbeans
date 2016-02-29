/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.markiewb.netbeans.plugins.eclipse.formatter;

/**
 *
 * @author markiewb
 */
public final class Pair implements Comparable<Pair> {

    public static Pair of(int first, int second) {
        return new Pair(first, second);
    }
    private int first;
    private int second;

    private Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(Pair b) {
        Pair a = this;
        int firstComp = a.first - b.first;
        if (0 != firstComp) {
            return firstComp;
        }
        int secondComp = a.second - b.second;
        return secondComp;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }

}
