package me.spencernold.jrs;

class Binding {

    static {
        System.loadLibrary("binding");
    }

    public static native int tun_create();
    public static native void tun_free(int fd);
}
