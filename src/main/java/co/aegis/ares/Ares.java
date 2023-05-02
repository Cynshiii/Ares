package co.aegis.ares;

import co.aegis.ares.framework.features.Features;
import co.aegis.ares.listeners.common.TemporaryListener;
import co.aegis.ares.utils.EnumUtils;
import co.aegis.ares.utils.Env;
import co.aegis.ares.utils.ReflectionUtils;
import co.aegis.ares.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class Ares extends JavaPlugin {

    @Getter
    private Features features;
    private static Ares instance;
    @Getter
    private static Thread thread;
    private static API api;

    public Ares() {
        if (instance == null) {
            instance = this;
        } else
                Bukkit.getServer().getLogger().info("Ares could not be initialized: Instance is not null but is: " + instance.getClass().getName());

        api = new API();
    }

    public static Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    public static <T> T singletonOf(Class<T> clazz) {
        return (T) singletons.computeIfAbsent(clazz, $ -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
                Ares.log(Level.FINE, "Failed to create singleton of " + clazz.getName() + ", falling back to Objenesis", ex);
                try {
                    return new ObjenesisStd().newInstance(clazz);
                } catch (Throwable t) {
                    throw new IllegalStateException("Failed to create singleton of " + clazz.getName() + " using Objenesis", t);
                }
            }
        });
    }

    @Getter
    private static final List<Listener> listeners = new ArrayList<>();
    @Getter
    private static final List<TemporaryListener> temporaryListeners = new ArrayList<>();
    @Getter
    private static final List<Class<? extends Event>> eventHandlers = new ArrayList<>();

    public static void registerListener(Listener listener) {
        if (!Utils.canEnable(listener.getClass()))
            return;

        final boolean isTemporary = listener instanceof TemporaryListener;
        if (listeners.contains(listener) && !isTemporary) {
            Ares.debug("Ignoring duplicate listener registration for class " + listener.getClass().getSimpleName());
            return;
        }

        if (getInstance().isEnabled()) {
            getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
            listeners.add(listener);
            if (!isTemporary)
                for (Method method : ReflectionUtils.methodsAnnotatedWith(listener.getClass(), EventHandler.class))
                    eventHandlers.add((Class<? extends Event>) method.getParameters()[0].getType());
        } else
            log("Could not register listener " + listener.getClass().getName() + "!");
    }

    public static Ares getInstance() {
        if (instance == null)
            Bukkit.getServer().getLogger().info("Ares could not be initialized");
        return instance;
    }

    @Getter
    @Setter
    private static boolean debug = true;

    public static void debug(String message) {
        if (debug)
            getInstance().getLogger().info("[DEBUG] " + net.md_5.bungee.api.ChatColor.stripColor(message));
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(String message, Throwable ex) {
        log(Level.INFO, message, ex);
    }

    public static void warn(String message) {
        log(Level.WARNING, message);
    }

    public static void warn(String message, Throwable ex) {
        log(Level.WARNING, message, ex);
    }

    public static void severe(String message) {
        log(Level.SEVERE, message);
    }

    public static void severe(String message, Throwable ex) {
        log(Level.SEVERE, message, ex);
    }

    public static void log(Level level, String message) {
        log(level, message, null);
    }

    public static void log(Level level, String message, Throwable ex) {
        getInstance().getLogger().log(level, ChatColor.stripColor(message), ex);
    }

    public static Env getEnv() {
        String env = getInstance().getConfig().getString("env", Env.DEV.name()).toUpperCase();
        try {
            return Env.valueOf(env);
        } catch (IllegalArgumentException ex) {
            Ares.severe("Could not parse environment variable " + env + ", options are: " + EnumUtils.valueNamesPretty(Env.class));
            Ares.severe("Defaulting to " + Env.DEV.name() + " environment");
            return Env.DEV;
        }
    }

    public final static String colorSecondary = "&#FF7778";
    public final static String colorPrimary = "&#90A3FF";
    public final static String colorAccent = "&#FFFFFF";

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
