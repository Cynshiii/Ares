package co.aegis.ares.framework.features;

import co.aegis.ares.utils.StringUtils;

public abstract class Feature {
    public String PREFIX = StringUtils.getPrefix(getName());
    public String getName() {
        return Features.prettyName(this);
    }

    public String getPrefix() {
        return PREFIX;
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void reload() {
        onStop();
        onStart();
    }

}