package ai.chat2db.community.domain.core.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ListSorter {


    public static <T> void sortByKey(List<T> list, Function<T, String> keyExtractor) {
        if (list == null || list.size() <= 1) {
            return;
        }
        list.sort(Comparator.comparing(keyExtractor, String.CASE_INSENSITIVE_ORDER));
    }
}
