package co.aegis.ares.utils;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    @Getter
    protected static final Random random = new Random();

    @Contract("null -> null")
    public static <T> T randomElement(T @Nullable ... list) {
        if (list == null || list.length == 0)
            return null;
        return list[random.nextInt(list.length)];
    }

    @Contract("null -> null")
    public static <T> T randomElement(@Nullable Collection<@Nullable T> list) {
        if (Nullables.isNullOrEmpty(list)) return null;
        int getIndex = random.nextInt(list.size());
        int currentIndex = 0;
        for (T item : list) {
            if (currentIndex++ == getIndex)
                return item;
        }
        throw new IllegalStateException("Collection was altered during iteration");
    }

    public static <T> T randomElement(@NotNull Class<? extends T> enumClass) {
        return randomElement(enumClass.getEnumConstants());
    }

    @Contract("null -> null")
    private static <T> T randomElement(@Nullable List<@Nullable T> list) {
        if (Nullables.isNullOrEmpty(list)) return null;
        return list.get(random.nextInt(list.size()));
    }

}
