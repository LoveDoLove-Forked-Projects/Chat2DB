package ai.chat2db.spi.util;

import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class IntervalUtil {
    public static List<Interval> sortIntervals(HashSet<Interval> intervals) {
        List<Interval> intervalList = new ArrayList<>(intervals);
        intervalList.sort((i1, i2) -> {
            if (i1.a != i2.a) {
                return Integer.compare(i1.a, i2.a);
            } else {
                return Integer.compare(i1.b, i2.b);
            }
        });
        return intervalList;
    }
}
