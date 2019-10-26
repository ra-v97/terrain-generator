package common;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Iterables {
    public static <T> Stream<T> stream(Iterable<T> i) {
        return StreamSupport.stream(i.spliterator(), false);
    }
}
