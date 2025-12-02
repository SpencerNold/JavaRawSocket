package me.spencernold.jrs;

public class Main {

    public static void main(String[] args) {
        String device = PacketCaptureBinding.getDefaultDevice();
        System.out.println(device);
    }
}
