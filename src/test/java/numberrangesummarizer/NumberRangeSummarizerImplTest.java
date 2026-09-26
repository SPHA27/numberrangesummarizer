package numberrangesummarizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberRangeSummarizerImplTest {

    private NumberRangeSummarizer summarizer;

    @BeforeEach
    void setUp() {
        summarizer = new NumberRangeSummarizerImpl();
    }

    // ---- collect() ----

    @Test
    void collect_parsesCommaDelimitedIntegers() {
        Collection<Integer> result = summarizer.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");
        assertEquals(
                Arrays.asList(1, 3, 6, 7, 8, 12, 13, 14, 15, 21, 22, 23, 24, 31),
                new java.util.ArrayList<>(result)
        );
    }

    @Test
    void collect_toleratesWhitespaceAroundTokens() {
        Collection<Integer> result = summarizer.collect(" 1 , 3 ,6");
        assertEquals(Arrays.asList(1, 3, 6), new java.util.ArrayList<>(result));
    }

    @Test
    void collect_nullInput_returnsEmptyCollection() {
        assertTrue(summarizer.collect(null).isEmpty());
    }

    @Test
    void collect_blankInput_returnsEmptyCollection() {
        assertTrue(summarizer.collect("   ").isEmpty());
    }

    // ---- summarizeCollection() ----

    @Test
    void summarize_matchesSampleFromSpec() {
        Collection<Integer> input = summarizer.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");
        assertEquals("1, 3, 6-8, 12-15, 21-24, 31", summarizer.summarizeCollection(input));
    }

    @Test
    void summarize_emptyCollection_returnsEmptyString() {
        assertEquals("", summarizer.summarizeCollection(Collections.emptyList()));
    }

    @Test
    void summarize_nullCollection_returnsEmptyString() {
        assertEquals("", summarizer.summarizeCollection(null));
    }

    @Test
    void summarize_singleNumber_isNotFormattedAsARange() {
        assertEquals("5", summarizer.summarizeCollection(Collections.singletonList(5)));
    }

    @Test
    void summarize_allSequential_formsOneRange() {
        assertEquals("1-5", summarizer.summarizeCollection(Arrays.asList(1, 2, 3, 4, 5)));
    }

    @Test
    void summarize_noSequentialNumbers_listsEachSeparately() {
        assertEquals("1, 3, 5, 7", summarizer.summarizeCollection(Arrays.asList(1, 3, 5, 7)));
    }

    @Test
    void summarize_unsortedInput_isSortedBeforeSummarizing() {
        assertEquals("1-3, 7", summarizer.summarizeCollection(Arrays.asList(7, 2, 1, 3)));
    }

    @Test
    void summarize_duplicateValues_areCollapsed() {
        assertEquals("1-3", summarizer.summarizeCollection(Arrays.asList(1, 2, 2, 3, 3, 3)));
    }

    @Test
    void summarize_negativeAndPositiveNumbersInOneRun_formOneRange() {
        // -3..2 is fully consecutive, so it collapses into a single range.
        assertEquals("-3-2", summarizer.summarizeCollection(Arrays.asList(-3, -2, -1, 0, 1, 2)));
    }

    @Test
    void summarize_negativeRangeWithGap_producesTwoRanges() {
        assertEquals("-3--1, 5-7", summarizer.summarizeCollection(Arrays.asList(-3, -2, -1, 5, 6, 7)));
    }

    @Test
    void summarize_twoElementSequentialRun_isARange() {
        assertEquals("4-5", summarizer.summarizeCollection(Arrays.asList(4, 5)));
    }

    @Test
    void summarize_collectThenSummarize_endToEnd() {
        List<Integer> numbers = new java.util.ArrayList<>(summarizer.collect("10,11,9,2,4,3"));
        assertEquals("2-4, 9-11", summarizer.summarizeCollection(numbers));
    }
}
