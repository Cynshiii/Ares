package co.aegis.ares.listeners;

import co.aegis.ares.framework.features.Feature;

import static co.aegis.ares.utils.Utils.registerListeners;

public class Listeners extends Feature {

    @Override
    public void onStart() {
        registerListeners(getClass().getPackageName());
    }

}
