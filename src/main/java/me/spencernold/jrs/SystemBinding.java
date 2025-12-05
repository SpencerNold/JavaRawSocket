package me.spencernold.jrs;

public class SystemBinding extends Binding {

    static {
        ensureBindingLoaded();
    }

    public static native int getIPv4Address();
    public static native byte[] getMacAddress();
    public static native int getDefaultGateway();
}
