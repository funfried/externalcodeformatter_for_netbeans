/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui.editor.diff;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.netbeans.api.diff.Difference;

/**
 * Implementation based on NetBeans' org.netbeans.modules.diff.builtin.provider.HuntDiff
 * but without options, it always takes whitespaces into account.
 *
 * @author bahlef
 */
public class Diff {
	/**
	 * Private constructor due to static methods.
	 */
	private Diff() {
	}

	/**
	 * Read the lines of the given {@link Reader} into a {@link String} array.
	 *
	 * @param r the {@link Reader}
	 *
	 * @return a {@link String} array containing each line of the {@link Reader} as a entry of the array
	 *
	 * @throws IOException if a problem occurs while reading for the given {@link Reader}
	 */
	private static String[] getLines(Reader r) throws IOException {
		List<String> lines = IOUtils.readLines(r);

		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * @param r1 {@link Reader} from the first source
	 * @param r2 {@link Reader} from the second source
	 *
	 * @return computed diff
	 *
	 * @throws IOException if there is an I/O issue with the given {@link Reader}s
	 */
	public static Difference[] diff(Reader r1, Reader r2) throws IOException {
		return diff(getLines(r1), getLines(r2));
	}

	/**
	 * @param lines1 array of lines from the first source
	 * @param lines2 array of lines from the second source
	 *
	 * @return computed diff
	 */
	public static Difference[] diff(String[] lines1, String[] lines2) {
		int[] J = prepareIndex(lines1, lines2);
		List<Difference2> differences = getDifferences(J, lines1, lines2);
		cleanup(differences);
		Difference2[] tempDiffs = differences.toArray(new Difference2[differences.size()]);
		Difference[] diffs = new Difference[tempDiffs.length];
		for (int i = 0; i < tempDiffs.length; ++i) {
			Difference2 tempDiff = tempDiffs[i];
			tempDiffs[i] = null;
			diffs[i] = new Difference(tempDiff.getType(), tempDiff.getFirstStart(), tempDiff.getFirstEnd(),
					tempDiff.getSecondStart(), tempDiff.getSecondEnd(),
					tempDiff.getFirstText(), tempDiff.getSecondText());
		}
		return diffs;
	}

	private static int[] prepareIndex(String[] lines1, String[] lines2) {
		int m = lines1.length;
		int n = lines2.length;

		Line[] l2s = new Line[n + 1];
		// In l2s we have sorted lines of the second file <1, n>
		for (int i = 1; i <= n; i++) {
			l2s[i] = new Line(i, lines2[i - 1]);
		}
		Arrays.sort(l2s, 1, n + 1, (Line l1, Line l2) -> l1.line.compareTo(l2.line));

		int[] equvalenceLines = new int[n + 1];
		boolean[] equivalence = new boolean[n + 1];
		for (int i = 1; i <= n; i++) {
			Line l = l2s[i];
			equvalenceLines[i] = l.lineNo;
			equivalence[i] = i == n || !l.line.equals(l2s[i + 1].line);//((Line) l2s.get(i)).line);
		}
		equvalenceLines[0] = 0;
		equivalence[0] = true;
		int[] equivalenceAssoc = new int[m + 1];
		for (int i = 1; i <= m; i++) {
			equivalenceAssoc[i] = findAssoc(lines1[i - 1], l2s, equivalence);
		}

		Candidate[] K = new Candidate[Math.min(m, n) + 2];
		K[0] = new Candidate(0, 0, null);
		K[1] = new Candidate(m + 1, n + 1, null);
		int k = 0;
		for (int i = 1; i <= m; i++) {
			if (equivalenceAssoc[i] != 0) {
				k = merge(K, k, i, equvalenceLines, equivalence, equivalenceAssoc[i]);
			}
		}
		int[] J = new int[m + 2]; // Initialized with zeros

		Candidate c = K[k];
		while (c != null) {
			J[c.a] = c.b;
			c = c.c;
		}
		return J;
	}

	private static int findAssoc(String line1, Line[] l2s, boolean[] equivalence) {
		int idx = binarySearch(l2s, line1, 1, l2s.length - 1);
		if (idx < 1) {
			return 0;
		} else {
			int lastGoodIdx = 0;
			for (; idx >= 1 && l2s[idx].line.equals(line1); idx--) {
				if (equivalence[idx - 1]) {
					lastGoodIdx = idx;
				}
			}
			return lastGoodIdx;
		}
	}

	private static int binarySearch(Line[] L, String key, int low, int high) {
		while (low <= high) {
			int mid = (low + high) >>> 1;
			String midVal = L[mid].line;
			int comparison = midVal.compareTo(key);
			if (comparison < 0) {
				low = mid + 1;
			} else if (comparison > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -(low + 1);
	}

	private static int binarySearch(Candidate[] K, int key, int low, int high) {
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = K[mid].b;
			if (midVal < key) {
				low = mid + 1;
			} else if (midVal > key) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -(low + 1);
	}

	private static int merge(Candidate[] K, int k, int i, int[] equvalenceLines,
			boolean[] equivalence, int p) {
		int r = 0;
		Candidate c = K[0];
		do {
			int j = equvalenceLines[p];
			int s = binarySearch(K, j, r, k);
			if (s >= 0) {
				// j was found in K[]
				s = k + 1;
			} else {
				s = -s - 2;
				if (s < r || s > k)
					s = k + 1;
			}
			if (s <= k) {
				if (K[s + 1].b > j) {
					Candidate newc = new Candidate(i, j, K[s]);
					K[r] = c;
					r = s + 1;
					c = newc;
				}
				if (s == k) {
					K[k + 2] = K[k + 1];
					k++;
					break;
				}
			}
			if (equivalence[p]) {
				break;
			} else {
				p++;
			}
		} while (true);
		K[r] = c;
		return k;
	}

	private static List<Difference2> getDifferences(int[] J, String[] lines1, String[] lines2) {
		List<Difference2> differences = new ArrayList<>();
		int n = lines1.length;
		int m = lines2.length;
		int start1 = 1;
		int start2 = 1;
		do {
			while (start1 <= n && J[start1] == start2) {
				start1++;
				start2++;
			}
			if (start1 > n)
				break;
			if (J[start1] < start2) { // There's something extra in the first file
				int end1 = start1 + 1;
				List<String> deletedLines = new ArrayList<>();
				deletedLines.add(lines1[start1 - 1]);
				while (end1 <= n && J[end1] < start2) {
					String line = lines1[end1 - 1];
					deletedLines.add(line);
					end1++;
				}
				differences.add(new Difference2(Difference.DELETE, start1, end1 - 1, start2 - 1, 0,
						deletedLines.toArray(new String[deletedLines.size()]), null));
				start1 = end1;
			} else { // There's something extra in the second file
				int end2 = J[start1];
				List<String> addedLines = new ArrayList<>();
				for (int i = start2; i < end2; i++) {
					String line = lines2[i - 1];
					addedLines.add(line);
				}
				differences.add(new Difference2(Difference.ADD, start1 - 1, 0, start2, end2 - 1,
						null, addedLines.toArray(new String[addedLines.size()])));
				start2 = end2;
			}
		} while (start1 <= n);
		if (start2 <= m) { // There's something extra at the end of the second file
			int end2 = start2 + 1;
			List<String> addedLines = new ArrayList<>();
			addedLines.add(lines2[start2 - 1]);
			while (end2 <= m) {
				String line = lines2[end2 - 1];
				addedLines.add(line);
				end2++;
			}
			differences.add(new Difference2(Difference.ADD, n, 0, start2, m,
					null, addedLines.toArray(new String[addedLines.size()])));
		}
		return differences;
	}

	private static void cleanup(List<Difference2> diffs) {
		Difference2 last = null;
		for (int i = 0; i < diffs.size(); i++) {
			Difference2 diff = diffs.get(i);
			if (last != null && (diff.getType() == Difference.ADD && last.getType() == Difference.DELETE ||
					diff.getType() == Difference.DELETE && last.getType() == Difference.ADD)) {
				Difference2 add;
				Difference2 del;
				if (Difference.ADD == diff.getType()) {
					add = diff;
					del = last;
				} else {
					add = last;
					del = diff;
				}
				int d1f1l1 = add.getFirstStart() - (del.getFirstEnd() - del.getFirstStart());
				int d2f1l1 = del.getFirstStart();
				if (d1f1l1 == d2f1l1) {
					Difference2 newDiff = new Difference2(Difference.CHANGE,
							d1f1l1, del.getFirstEnd(), add.getSecondStart(), add.getSecondEnd(),
							del.getFirstLines(), add.getSecondLines());
					diffs.set(i - 1, newDiff);
					diffs.remove(i);
					i--;
					diff = newDiff;
				}
			}
			last = diff;
		}
	}

	private static class Line {
		private final int lineNo;

		private final String line;

		public Line(int lineNo, String line) {
			this.lineNo = lineNo;
			this.line = line;
		}
	}

	private static class Candidate {
		private final int a;

		private final int b;

		private final Candidate c;

		public Candidate(int a, int b, Candidate c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	/**
	 * An intermediate difference instance sharing left and right content with
	 * the original lines to save memory until the real merged content is
	 * requested.
	 */
	private static class Difference2 extends Difference {
		private final String[] firstLines;

		private final String[] secondLines;

		private String leftText;

		private String rightText;

		public Difference2(int type, int firstStart, int firstEnd, int secondStart, int secondEnd,
				String[] lines1, String[] lines2) {
			super(type, firstStart, firstEnd, secondStart, secondEnd);
			this.firstLines = lines1;
			this.secondLines = lines2;
		}

		public String[] getFirstLines() {
			return firstLines;
		}

		public String[] getSecondLines() {
			return secondLines;
		}

		@Override
		public String getFirstText() {
			if (leftText == null && firstLines != null) {
				leftText = mergeLines(firstLines);
			}
			return leftText;
		}

		@Override
		public String getSecondText() {
			if (rightText == null && secondLines != null) {
				rightText = mergeLines(secondLines);
			}
			return rightText;
		}

	}

	private static String mergeLines(String[] lines) {
		int capacity = lines.length; // for newlines
		for (String line : lines) {
			capacity += line.length();
		}
		StringBuilder sb = new StringBuilder(capacity);
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			lines[i] = null;
			sb.append(line).append('\n');
		}
		return sb.toString();
	}
}
