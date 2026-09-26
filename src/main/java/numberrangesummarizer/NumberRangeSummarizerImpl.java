package numberrangesummarizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link NumberRangeSummarizer}.
 *
 * <p>Assumptions (see unit tests for executable versions of each):</p>
 * <ul>
 *     <li>{@link #collect(String)} parses a comma delimited string of integers,
 *     tolerating surrounding whitespace around each token (e.g. "1, 3 ,6").
 *     A {@code null} or blank input yields an empty collection.</li>
 *     <li>{@link #summarizeCollection(Collection)} treats the input as a set of
 *     values to be reported in ascending order: duplicates are collapsed and the
 *     collection does not need to arrive pre-sorted.</li>
 *     <li>A "range" requires at least two consecutive integers (each exactly one
 *     more than the previous). A single, isolated number is printed on its own,
 *     e.g. "3" rather than "3-3".</li>
 *     <li>Negative numbers are supported and ordered numerically (e.g. -3 before -1).</li>
 *     <li>A {@code null} or empty input collection summarizes to an empty string.</li>
 * </ul>
 */
public class NumberRangeSummarizerImpl implements NumberRangeSummarizer {

    private static final String INPUT_DELIMITER = ",";
    private static final String OUTPUT_DELIMITER = ", ";
    private static final String RANGE_SEPARATOR = "-";

    @Override
    public Collection<Integer> collect(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(input.split(INPUT_DELIMITER))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    @Override
    public String summarizeCollection(Collection<Integer> input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Sorted + de-duplicated view of the input, per the class-level assumptions.
        List<Integer> numbers = new ArrayList<>(new TreeSet<>(input));

        List<String> segments = new ArrayList<>();
        int rangeStart = numbers.get(0);
        int rangeEnd = rangeStart;

        for (int i = 1; i < numbers.size(); i++) {
            int current = numbers.get(i);

            if (current == rangeEnd + 1) {
                // Still consecutive: extend the current run.
                rangeEnd = current;
            } else {
                // Run broken: close off the current segment and start a new one.
                segments.add(formatSegment(rangeStart, rangeEnd));
                rangeStart = current;
                rangeEnd = current;
            }
        }
        segments.add(formatSegment(rangeStart, rangeEnd));

        return String.join(OUTPUT_DELIMITER, segments);
    }

    private String formatSegment(int start, int end) {
        return start == end
                ? String.valueOf(start)
                : start + RANGE_SEPARATOR + end;
    }
}
