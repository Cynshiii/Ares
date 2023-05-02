package co.aegis.ares.utils;

import co.aegis.ares.Ares;
import co.aegis.ares.framework.annotations.Disabled;
import co.aegis.ares.framework.annotations.Environments;
import io.github.classgraph.AnnotationEnumValue;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static co.aegis.ares.utils.ReflectionUtils.methodsAnnotatedWith;
import static co.aegis.ares.utils.ReflectionUtils.subTypesOf;

public class Utils {

    public static void registerListeners(Package packageObject) {
        registerListeners(packageObject.getName());
    }

    public static void registerListeners(String packageName) {
        subTypesOf(Listener.class, packageName).forEach(Utils::tryRegisterListener);
    }

    public static void tryRegisterListener(Class<?> clazz) {
        if (canEnable(clazz))
            tryRegisterListener(Ares.singletonOf(clazz));
    }

    public static void tryRegisterListener(Object object) {
        try {
            final Class<?> clazz = object.getClass();
            if (!canEnable(clazz))
                return;

            boolean hasNoArgsConstructor = Stream.of(clazz.getConstructors()).anyMatch(c -> c.getParameterCount() == 0);
            if (object instanceof Listener listener) {
                if (hasNoArgsConstructor)
                    Ares.registerListener(listener);
                else
                    Ares.warn("Cannot register listener on " + clazz.getSimpleName() + ", needs @NoArgsConstructor");
            } else if (methodsAnnotatedWith(clazz, EventHandler.class).size() > 0)
                Ares.warn("Found @EventHandlers in " + clazz.getSimpleName() + " which does not implement Listener"
                        + (hasNoArgsConstructor ? "" : " or have a @NoArgsConstructor"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean canEnable(Class<?> clazz) {
        if (clazz.getSimpleName().startsWith("_"))
            return false;
        if (Modifier.isAbstract(clazz.getModifiers()))
            return false;
        if (Modifier.isInterface(clazz.getModifiers()))
            return false;
        if (clazz.getAnnotation(Disabled.class) != null)
            return false;
        if (clazz.getAnnotation(Environments.class) != null && !Env.applies(clazz.getAnnotation(Environments.class).value()))
            return false;

        return true;
    }

    public static boolean canEnable(ClassInfo clazz) {
        if (clazz.getSimpleName().startsWith("_"))
            return false;
        if (Modifier.isAbstract(clazz.getModifiers()))
            return false;
        if (Modifier.isInterface(clazz.getModifiers()))
            return false;
        if (clazz.getAnnotationInfo(Disabled.class) != null)
            return false;

        final AnnotationInfo environments = clazz.getAnnotationInfo(Environments.class);
        if (environments != null) {
            final List<Env> envs = Arrays.stream((Object[]) environments.getParameterValues().get("value").getValue())
                    .map(obj -> (AnnotationEnumValue) obj)
                    .map(value -> Env.valueOf(value.getValueName()))
                    .toList();

            if (!Env.applies(envs))
                return false;
        }

        return true;
    }


    public static <T> List<T> reverse(List<T> list) {
        Collections.reverse(list);
        return list;
    }

    public static boolean isLong(String text) {
        try {
            Long.parseLong(text);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
