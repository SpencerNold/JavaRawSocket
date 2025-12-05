package me.spencernold.jrs;

import java.io.IOException;

public class SystemBinding extends Binding {

    static {
        ensureBindingLoaded();
    }

    public static native int getIPv4Address() throws IOException;
    public static native byte[] getMacAddress() throws IOException;
    public static native int getDefaultGateway() throws IOException;
    public static native byte[] getDeviceMacAddress(int address) throws IOException;
}
