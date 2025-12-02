package me.spencernold.jrs;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String device = PacketCaptureBinding.getDefaultDevice();
            long handle = PacketCaptureBinding.open(device);
            PacketCaptureBinding.listen(handle, (h, bytes, length) -> {
                System.out.println("Length: " + length);
            });
            PacketCaptureBinding.close(handle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
