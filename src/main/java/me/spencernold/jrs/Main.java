package me.spencernold.jrs;

public class Main {

    static {
        System.loadLibrary("binding");
    }

    public static native String helloFromCpp();

    public static void main(String[] args) {
        System.out.println(helloFromCpp());
    }
}
