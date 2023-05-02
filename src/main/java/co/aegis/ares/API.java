package co.aegis.ares;

import co.aegis.ares.utils.Env;

import java.util.Optional;

public class API {

    protected static API instance;

    public API() {
        instance = this;
    }

    public static API get() {
        return instance;
    }

    public Env getEnv() {
        return Ares.getEnv();
    }

    public static Optional<API> getOptional() {
        return Optional.of(instance);
    }

    public ClassLoader getClassLoader() {
        return Ares.class.getClassLoader();
    }
}
