package co.aegis.ares.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumUtils {

    public static <T> List<String> valueNameList(Class<? extends T> clazz) {
        return Arrays.stream(clazz.getEnumConstants()).map(value -> ((Enum<?>) value).name().toLowerCase()).collect(Collectors.toList());
    }

    public static <T> String valueNamesPretty(Class<? extends T> clazz) {
        return String.join(", ", valueNameList(clazz));
    }

}
