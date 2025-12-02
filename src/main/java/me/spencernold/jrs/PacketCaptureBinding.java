package me.spencernold.jrs;

class PacketCaptureBinding {

    static {
        System.loadLibrary("binding");
    }

    public static native String getDefaultDevice();
}
