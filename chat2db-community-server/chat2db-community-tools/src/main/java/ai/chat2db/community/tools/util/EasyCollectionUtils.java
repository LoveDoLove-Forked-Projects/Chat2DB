package ai.chat2db.community.tools.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;


public class EasyCollectionUtils {


    public static <T> Stream<T> stream(Collection<T> collection) {
        return collection != null ? collection.stream() : Stream.empty();
    }


    public static <T> T findFirst(Collection<T> collection) {
        return stream(collection)
                .findFirst()
                .orElse(null);
    }


    public static <T, R> List<R> toList(Collection<T> collection, Function<T, R> function) {
        return stream(collection)
                .filter(Objects::nonNull)
                .map(function)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static <T, R> Set<R> toSet(Collection<T> collection, Function<T, R> function) {
        return stream(collection)
                .filter(Objects::nonNull)
                .map(function)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    public static <K, V, T> Map<K, V> toMap(Collection<T> collection, Function<? super T, K> keyFunction,
                                            Function<? super T, V> valueFunction) {
        return stream(collection)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyFunction, valueFunction, (oldValue, newValue) -> newValue));
    }


    public static <K, T> Map<K, T> toIdentityMap(Collection<T> collection, Function<? super T, K> keyFunction) {
        return toMap(collection, keyFunction, Function.identity());
    }


    public static <C> boolean addAll(final Collection<C> collection, final Collection<C> collectionAdd) {
        if (collectionAdd == null) {
            return false;
        }
        return collection.addAll(collectionAdd);
    }


    public static boolean isEmptyButNotNull(final Collection<?> collection) {
        return collection != null && collection.isEmpty();
    }


    public static boolean isAnyEmptyButNotNull(final Collection<?>... collections) {
        if (ArrayUtils.isEmpty(collections)) {
            return false;
        }
        for (final Collection<?> collection : collections) {
            if (isEmptyButNotNull(collection)) {
                return true;
            }
        }
        return false;
    }


    public static <T> void add(Collection<T> collection, T objectAdd) {
        if (Objects.isNull(objectAdd)) {
            return;
        }
        collection.add(objectAdd);
    }


    public static <E, R> List<E> distinctByKey(Collection<E> collection, Function<E, R> keyFunction) {
        return stream(collection).filter(distinctByKey(keyFunction)).collect(Collectors.toList());
    }


    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    public static <E> List<E> union(List<? extends E> list1, List<? extends E> list2) {
        ArrayList<E> result = new ArrayList();
        if (list1 != null && list1.size() > 0) {
            result.addAll(list1);
        }
        if (list2 != null && list2.size() > 0) {
            result.addAll(list2);
        }
        return result;
    }


}
