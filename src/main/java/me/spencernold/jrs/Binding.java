package me.spencernold.jrs;

class Binding {

    private static boolean loaded;

    protected static void ensureBindingLoaded() {
        if (loaded)
            return;
        loaded = true;
        System.loadLibrary("binding");
    }
}
