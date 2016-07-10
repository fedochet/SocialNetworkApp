package utils;

import java.util.function.Function;

/**
 * Created by roman on 10.07.2016.
 */
public interface GeneralUtils {
    static <T,B> B mapOrNull(T valueToCheck, Function<T,B> map) {
        return mapOrElse(valueToCheck, map, null);
    }

    static <T,B> B mapOrElse(T valueToCheck, Function<T,B> map, B defaultValue) {
        if (valueToCheck == null)
            return defaultValue;

        return map.apply(valueToCheck);
    }
}
