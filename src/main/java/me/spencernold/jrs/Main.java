package me.spencernold.jrs;

public class Main {

    public static void main(String[] args) {
        byte[] mac = MediumAccessControl.getRouterMac();
        System.out.println(MediumAccessControl.decode(mac));
    }
}
