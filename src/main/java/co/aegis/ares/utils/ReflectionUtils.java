package co.aegis.ares.utils;

import co.aegis.ares.API;
import co.aegis.ares.Ares;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectionUtils {


    private static <T> Set<Class<? extends T>> getClasses(String[] packages, Predicate<ClassInfo> filter) {
        ClassGraph classGraph = scanPackages(packages);

        try (var scan = classGraph.scan()) {

            ClassInfoList classes = scan.getAllClasses();

            return classes.stream()
                    .filter(filter)
                    .map(ClassInfo::loadClass)
                    .map(clazz -> (Class<? extends T>) clazz)
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            Ares.log("getClasses error");
            e.printStackTrace();
        }

        return new HashSet<>();
    }

    public static <T> List<Class<? extends T>> superclassesOf(Class<? extends T> clazz) {
        List<Class<? extends T>> superclasses = new ArrayList<>();
        while (clazz.getSuperclass() != Object.class) {
            superclasses.add(clazz);
            clazz = (Class<? extends T>) clazz.getSuperclass();
        }

        superclasses.add(clazz);
        return superclasses;
    }

    public static <T> Set<Class<? extends T>> subTypesOf(Class<T> superclass, String... packages) {
        return getClasses(packages, subclass -> {
            if (!Utils.canEnable(subclass))
                return false;

            if (superclass.isInterface())
                return subclass.implementsInterface(superclass);
            else
                return subclass.extendsSuperclass(superclass);
        });
    }

    public static Set<Method> methodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        return new HashSet<>() {{
            for (Method method : getAllMethods(clazz)) {
                method.setAccessible(true);
                if (method.getAnnotation(annotation) != null)
                    add(method);
            }
        }};
    }

    private static ClassGraph scanPackages(String... packages) {
        final ClassGraph scanner = new ClassGraph()
                .acceptPackages(packages)
                .enableClassInfo()
                .enableAnnotationInfo()
                .initializeLoadedClasses();

        if (API.get().getClassLoader() != null)
            scanner.overrideClassLoaders(API.get().getClassLoader());

        return scanner;
    }

    private static Set<Method> getAllMethods(Class<?> clazz) {
        return new HashSet<>(new HashMap<String, Method>() {{
            for (Class<?> clazz : Utils.reverse(superclassesOf(clazz)))
                for (Method method : clazz.getDeclaredMethods())
                    put(getMethodKey(method), method);
        }}.values());
    }

    @NotNull
    private static String getMethodKey(Method method) {
        final String params = Arrays.stream(method.getParameters())
                .map(parameter -> parameter.getType().getSimpleName())
                .collect(Collectors.joining(","));

        return "%s(%s)".formatted(method.getName(), params);
    }

}
