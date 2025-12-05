package me.spencernold.jrs;

import java.io.IOException;

class PacketCaptureBinding extends Binding {

    static {
        ensureBindingLoaded();
    }

    public static native String getDefaultDevice() throws IOException;
    public static native long open(String device) throws IOException;
    public static native void send(long handle, byte[] bytes, int offset, int length);
    public static native void listen(long handle, Listener listener);
    public static native void ignore(long handle);
    public static native void close(long handle);

    @FunctionalInterface
    public interface Listener {
        void listen(long handle, byte[] bytes, int length);
    }
}
