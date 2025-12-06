package me.spencernold.jrs;

public class ChecksumBinding extends Binding {

    static {
        ensureBindingLoaded();
    }

    public static native short calculateIPv4Checksum(byte[] bytes, int offset, int length);
}
